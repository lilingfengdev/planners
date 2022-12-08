package com.bh.planners.core.kether

import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.PlannersAPI.plannersProfileIsLoaded
import com.bh.planners.api.common.Demand.Companion.toDemand
import com.bh.planners.core.kether.event.ActionEventParser
import com.bh.planners.core.kether.selector.Selector
import com.bh.planners.core.pojo.Context
import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.pojo.Session
import com.bh.planners.core.pojo.player.PlayerJob
import com.bh.planners.core.pojo.player.PlayerProfile
import com.bh.planners.util.StringNumber
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.adaptPlayer
import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.library.kether.QuestContext
import taboolib.library.kether.QuestReader
import taboolib.module.kether.*
import taboolib.platform.type.BukkitPlayer
import java.util.*
import java.util.concurrent.CompletableFuture

const val NAMESPACE = "Planners"

val namespaces = listOf(NAMESPACE, "kether")


fun ScriptFrame.getContext(): Context {
    return rootVariables().get<Context>("@Context").orElse(null) ?: error("Error running environment !")
}

fun ScriptFrame.getSession(): Session {
    return rootVariables().get<Session>("@Context").orElse(null) ?: error("Error running environment !")
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
fun ScriptFrame.player(): Player? {
    return getContext().executor.asPlayer()
}
fun ScriptFrame.skill(): PlayerJob.Skill {
    return getSkill()
}


fun ScriptFrame.getSkill(): PlayerJob.Skill {

    val context = getContext()

    if (context is Context.Impl) {
        return context.playerSkill
    }

    error("Error running environment !")
}

fun ScriptFrame.target(): Target {

    val optional = rootVariables().get<Target.Location>("@Origin")
    if (optional.isPresent) {
        return optional.get()
    }

    val executor = executor()
    if (executor is BukkitPlayer) {
        return executor.player.toTarget()
    }

    return Any().toLocation().toTarget()
}

fun ScriptFrame.toOriginLocation(): Target.Location {
    return target() as Target.Location
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
        it.forEach(call)
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


inline fun <reified T> ScriptFrame.runTransfer(action: ParsedAction<*>): CompletableFuture<T> {

    val future = CompletableFuture<T>()
    this.newFrame(action).run<Any>().thenAccept {

        if (T::class.java.isEnum) {
            catchRunning {
                future.complete(getEnum<T>(it.toString().trim().uppercase().replace(".", "_")))
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
                Float::class -> Coerce.toFloat(it)
                else -> it.toString()
            } as T
            future.complete(value)
        }
    }
    return future
}

inline fun <reified T> ScriptFrame.runTransfer0(action: ParsedAction<*>, crossinline call: (T) -> Unit) {
    runTransfer<T>(action).thenAccept { call(it) }
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

fun ScriptFrame.getEntity(selector: ParsedAction<*>): CompletableFuture<Entity?> {
    return createContainer(selector).thenApply { it.firstEntityTarget() }
}

fun ScriptFrame.getLocation(selector: ParsedAction<*>): CompletableFuture<Location> {
    return createContainer(selector).thenApply { it.firstLocationTarget() }
}

fun ScriptFrame.senderPlannerProfile(): PlayerProfile? {
    val player = script().sender!!.castSafely<Player>()!!

    if (!player.plannersProfileIsLoaded) {
        ScriptService.terminateQuest(script())
        return null
    }

    return player.plannersProfile
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
                future.complete(container.join(it))
            }

            is Entity -> future.complete(container.join(it.toTarget()))

            is Location -> future.complete(container.join(it.toTarget()))

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
    return tryGet(array, null) ?: error("the lack of '${array.map { it }}' cite target")
}

fun QuestReader.tryGet(array: Array<out String>, def: Any? = null): ParsedAction<*>? {
    return try {
        mark()
        expects(*array)
        this.nextParsedAction()
    } catch (e: Exception) {
        reset()
        if (def == null) {
            null
        } else literalAction(def)
    }
}

fun QuestReader.selector(): ParsedAction<*> = get(arrayOf("they", "the"))

fun QuestReader.selectorAction(): ParsedAction<*>? {
    return tryGet(arrayOf("they", "the"), null)
}