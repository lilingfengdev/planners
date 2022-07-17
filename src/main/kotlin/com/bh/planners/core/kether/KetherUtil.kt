package com.bh.planners.core.kether

import com.bh.planners.api.common.Demand.Companion.toDemand
import com.bh.planners.core.kether.event.ActionEventParser
import com.bh.planners.core.kether.selector.Selector
import com.bh.planners.core.pojo.Context
import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.createContainer
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.kether.game.ActionProjectile
import com.bh.planners.core.pojo.Session
import com.bh.planners.core.pojo.player.PlayerJob
import com.bh.planners.util.StringNumber
import com.google.common.base.Enums
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.info
import taboolib.common.reflect.Reflex.Companion.invokeMethod
import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.library.kether.QuestContext
import taboolib.library.kether.QuestReader
import taboolib.library.kether.actions.LiteralAction
import taboolib.module.kether.*
import taboolib.platform.type.BukkitPlayer
import java.util.*
import java.util.concurrent.CompletableFuture

const val NAMESPACE = "Planners"

val namespaces = listOf(NAMESPACE, "kether")


fun ScriptFrame.getContext(): Context {
    return rootVariables().get<Context>("@Session").orElse(null) ?: error("Error running environment !")
}

fun ScriptFrame.getSession(): Session {
    return rootVariables().get<Session>("@Session").orElse(null) ?: error("Error running environment !")
}

fun ScriptFrame.executor(): ProxyCommandSender {
    return getContext().executor
}

fun ProxyCommandSender.asPlayer(): Player? {
    if (this is BukkitPlayer) {
        return player
    }
    return null
}

fun ScriptFrame.asPlayer(): Player? {
    return getContext().executor.asPlayer()
}

fun ScriptFrame.skill(): PlayerJob.Skill {
    return getSkill()
}


fun ScriptFrame.getSkill(): PlayerJob.Skill {
    return rootVariables().get<PlayerJob.Skill>("@Skill").orElse(null) ?: error("Error running environment !")
}

fun ScriptFrame.toTarget(): Target? {
    val executor = executor()
    if (executor is BukkitPlayer) {
        return executor.player.toTarget()
    }
    return Any().toLocation().toTarget()
}

fun ScriptFrame.toOriginLocation(): Target.Location? {
    val optional = rootVariables().get<Target.Location>("@Origin")
    if (optional.isPresent) {
        return optional.get()
    }
    return toTarget() as Target.Location
}

fun ScriptFrame.rootVariables(): QuestContext.VarTable {
    var vars = variables()
    var parent = parent()
    while (parent.isPresent) {
        vars = parent.get().variables()
        parent = parent.get().parent()
    }
    return vars
}

fun Any?.increaseAny(any: Any): Any {
    this ?: return any
    return StringNumber(toString()).add(any.toString()).get()
}

fun evalKether(player: Player, action: String, skill: PlayerJob.Skill): String? {
    return try {
        KetherShell.eval(action, sender = adaptPlayer(player), namespace = namespaces) {
            this.rootFrame().variables()["@Skill"] = skill
        }.get()?.toString()
    } catch (e: Throwable) {
        e.printKetherErrorMessage()
        return null
    }
}


fun Any.toLocation(): Location {
    return when (this) {
        is Location -> this
        is String -> {
            val split = split(",")
            Location(
                Bukkit.getWorld(split[0]),
                Coerce.toDouble(split[1]),
                Coerce.toDouble(split[2]),
                Coerce.toDouble(split[3])
            )
        }

        else -> Location(null, 0.0, 0.0, 0.0)
    }
}

fun Location.toLocal(): String {
    return "${world!!.name},$x,$y,$z"
}

fun ScriptFrame.exec(selector: ParsedAction<*>, call: Target.() -> Unit) {
    createContainer(selector).thenAccept {
        it.targets.forEach(call)
    }
}

fun ScriptFrame.runAny(action: ParsedAction<*>, call: Any.() -> Unit): CompletableFuture<Void> {
    return this.newFrame(action).run<Any>().thenAccept(call)
}

inline fun <reified T> getEnum(value: String): T {
    val declaredMethod = T::class.java.getDeclaredMethod("valueOf", String::class.java)
    declaredMethod.isAccessible = true
    return declaredMethod.invoke(null, value) as T
}

inline fun <reified T> ScriptFrame.runTransfer(
    action: ParsedAction<*>,
    crossinline call: (T) -> Unit
): CompletableFuture<Void> {

    return this.newFrame(action).run<Any>().thenAccept {

        if (T::class.java.isEnum) {
            catchRunning {
                call(getEnum(it.toString().trim().uppercase()))
            }
            return@thenAccept
        }

        catchRunning {
            val value = when (T::class) {
                String::class -> Coerce.toString(it)
                Int::class -> Coerce.toInteger(it)
                Long::class -> Coerce.toLong(it)
                Boolean::class -> Coerce.toBoolean(it)
                Double::class -> Coerce.toDouble(it)
                else -> it.toString()
            } as T
            call(value)
        }
    }
}

fun ScriptFrame.execEntity(selector: ParsedAction<*>, call: Entity.() -> Unit) {
    exec(selector) {
        if (this is Target.Entity) {
            call(this.entity)
        }
    }
}

fun ScriptFrame.execLivingEntity(selector: ParsedAction<*>, call: LivingEntity.() -> Unit) {
    execEntity(selector) {
        if (this is LivingEntity) {
            call(this)
        }
    }
}

fun ScriptFrame.execLocation(selector: ParsedAction<*>, call: Location.() -> Unit) {
    exec(selector) {
        if (this is Target.Location) {
            call(this.value)
        }
    }
}

fun ScriptFrame.execPlayer(selector: ParsedAction<*>, call: Player.() -> Unit) {
    exec(selector) {
        if (this is Target.Entity) {
            call(this.entity as? Player ?: return@exec)
        }
    }
}

fun ScriptFrame.createContainer(selector: ParsedAction<*>): CompletableFuture<Target.Container> {
    val future = CompletableFuture<Target.Container>()
    this.newFrame(selector).run<Any>().thenAccept {
        val container = Target.Container()

        when (it) {
            is List<*> -> {

                val list = it.mapNotNull { entry ->
                    if (entry is Entity) {
                        entry.toTarget()
                    } else if (entry is String) {
                        Bukkit.getEntity(UUID.fromString(entry.toString()))?.toTarget()
                    } else error("Transfer $entry to target failed")
                }
                container.addAll(list)
                future.complete(container)
            }

            is Target.Container -> future.complete(it)

            is Target -> {
                future.complete(container.add(it))
            }

            is Entity -> future.complete(container.add(it.toTarget()))

            is Location -> future.complete(container.add(it.toTarget()))

            is UUID -> {
                val entity = Bukkit.getEntity(it)
                if (entity != null) {
                    container.add(entity.toTarget())
                }
                future.complete(container)
            }

            else -> {
                catchRunning {
                    val demand = it.toString().toDemand()
                    Selector.check(toOriginLocation(), getContext(), demand, container).thenAccept {
                        future.complete(container)
                    }
                }
            }
        }

    }
    return future
}

fun catchRunning(action: () -> Unit) {
    try {
        action()
    } catch (e: Exception) {
        e.printKetherErrorMessage()
    } catch (e: Throwable) {
        e.printKetherErrorMessage()
    }
}


fun <T> eventParser(resolve: (QuestReader) -> ScriptAction<T>): ActionEventParser {
    return ActionEventParser(resolve)
}

fun QuestReader.get(array: Array<String>): ParsedAction<*> {
    return tryGet(array, null) ?: error("the lack of '$array' cite target")
}

fun QuestReader.tryGet(array: Array<String>, def: Any? = null): ParsedAction<*>? {
    return try {
        mark()
        expects(*array)
        next(ArgTypes.ACTION)
    } catch (e: Exception) {
        reset()
        if (def == null) {
            null
        } else ParsedAction(LiteralAction<Any>(def))
    }
}

fun QuestReader.selector(): ParsedAction<*> = get(arrayOf("they", "the"))

fun QuestReader.selectorAction(): ParsedAction<*>? {
    return tryGet(arrayOf("they", "the"), null)
}