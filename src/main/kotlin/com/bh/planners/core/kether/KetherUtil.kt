package com.bh.planners.core.kether

import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.PlannersAPI.plannersProfileIsLoaded
import com.bh.planners.api.common.Demand.Companion.toDemand
import com.bh.planners.api.entity.ProxyEntity
import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.pojo.Context
import com.bh.planners.core.pojo.Session
import com.bh.planners.core.pojo.player.PlayerJob
import com.bh.planners.core.pojo.player.PlayerProfile
import com.bh.planners.core.selector.Selector
import com.bh.planners.util.StringNumber
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyCommandSender
import taboolib.common5.Coerce
import taboolib.library.kether.ParsedAction
import taboolib.library.kether.QuestContext
import taboolib.library.kether.QuestReader
import taboolib.module.kether.*
import taboolib.platform.type.BukkitPlayer
import java.util.*
import java.util.concurrent.CompletableFuture


const val NAMESPACE = "Planners"

const val ACTION_NULL = "__NULL__"

val namespaces = listOf(NAMESPACE, "kether")


fun ScriptFrame.getContext(): Context {
    return rootVariables().get<Context>("@context").orElse(null) ?: error("Error running environment !")
}

fun ScriptFrame.session(): Session {
    return rootVariables().get<Session>("@context").orElse(null) ?: error("Error running environment !")
}

fun ScriptFrame.executor(): ProxyCommandSender {
    return getContext().proxySender
}

fun ProxyCommandSender.bukkitPlayer(): Player? {
    if (this is BukkitPlayer) {
        return player
    }
    return null
}

fun ScriptFrame.bukkitTarget(): Target {
    return getContext().sender
}

fun ScriptFrame.bukkitPlayer(): Player? {
    return getContext().player
}

fun ScriptFrame.skill(): PlayerJob.Skill {
    return (getContext() as? Context.Impl)?.playerSkill ?: error("Error running environment !")
}

fun ScriptFrame.origin(): Target.Location {
    return getContext().origin as Target.Location
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

fun Location.local(separator: String = ""): String {
    return arrayOf(world!!.name,"$x","$y","$z").joinToString(separator)
}

fun ScriptFrame.exec(selector: ParsedAction<*>, call: Target.() -> Unit) {
    createContainer(selector).thenAccept {
        it.forEach(call)
    }
}

fun Location.toLocal(): String {
    return "${world!!.name} $x $y $z"
}

fun Location.toLocalXYZ(): String {
    return "$x,$y,$z"
}

fun ScriptFrame.runAny(action: ParsedAction<*>, call: Any.() -> Unit): CompletableFuture<Void> {
    return this.newFrame(action).run<Any>().thenAccept(call)
}

inline fun <reified T> getEnum(value: String): T {
    val declaredMethod = T::class.java.getDeclaredMethod("valueOf", String::class.java)
    declaredMethod.isAccessible = true
    return declaredMethod.invoke(null, value) as T
}


inline fun <reified T> ScriptFrame.read(action: ParsedAction<*>): CompletableFuture<T> {

    val future = CompletableFuture<T>()
    this.newFrame(action).run<Any>().thenAccept {

        if (T::class.java.isEnum) {
            catchRunning {
                future.complete(getEnum<T>(it.toString().trim().uppercase(Locale.getDefault()).replace(".", "_")))
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

inline fun <reified T> ScriptFrame.readAccept(action: ParsedAction<*>, crossinline call: (T) -> Unit) {
    read<T>(action).thenAccept { call(it) }
}

fun ScriptFrame.execEntity(selector: ParsedAction<*>, call: Entity.() -> Unit) {
    exec(selector) {
        if (this is Target.Entity) {
            call(this.bukkitEntity ?: return@exec)
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
            call(this.player ?: return@exec)
        }
    }
}

fun ScriptFrame.getEntity(selector: ParsedAction<*>): CompletableFuture<Entity?> {
    return createContainer(selector).thenApply { it.firstEntityTarget() }
}

fun ScriptFrame.getLocation(selector: ParsedAction<*>): CompletableFuture<Location> {
    return createContainer(selector).thenApply { it.firstLocation() }
}

fun ScriptFrame.senderPlannerProfile(): PlayerProfile? {
    val player = script().sender!!.castSafely<Player>()!!

    if (!player.isOnline || !player.plannersProfileIsLoaded) {
        ScriptService.terminateQuest(script())
        return null
    }

    return player.plannersProfile
}

fun ScriptFrame.containerOrOrigin(action: ParsedAction<*>?): CompletableFuture<Target.Container> {
    return container(action, origin())
}

fun ScriptFrame.containerOrEmpty(action: ParsedAction<*>?) : CompletableFuture<Target.Container> {
    return if (action != null) {
        createContainer(action)
    } else {
        CompletableFuture.completedFuture(Target.Container())
    }
}

fun ScriptFrame.containerOrSender(action: ParsedAction<*>?): CompletableFuture<Target.Container> {
    return container(action, bukkitTarget())
}

fun ScriptFrame.container(action: ParsedAction<*>?, default: Location?): CompletableFuture<Target.Container> {
    return container(action, default?.toTarget())
}

fun ScriptFrame.container(action: ParsedAction<*>?, default: Entity?): CompletableFuture<Target.Container> {
    return container(action, default?.toTarget())
}

fun ScriptFrame.container(action: ParsedAction<*>?, default: Target? = null): CompletableFuture<Target.Container> {
    return if (action != null) {
        createContainer(action)
    } else {
        val future = CompletableFuture<Target.Container>()
        val container = Target.Container()
        if (default != null) {
            container += default
        }
        future.complete(container)
        future
    }
}

fun parseTargetContainer(value: Any, context: Context): Target.Container {

    val container = Target.Container()

    when (value) {

        is Target.Container -> container.addAll(value)

        is List<*> -> {
            value.filterNotNull().forEach {
                container += parseTargetContainer(it, context)
            }
        }

        is Target -> {
            container += value
        }

        is Entity -> {
            container += value.toTarget()
        }

        is Location -> {
            container += value.toTarget()
        }

        is UUID -> {
            Bukkit.getEntity(value)?.let { container += it.toTarget() }
        }

        else -> {
            container += Selector.check(context, value.toString().toDemand())
        }
    }
    return container
}


fun ScriptFrame.createContainer(selector: ParsedAction<*>): CompletableFuture<Target.Container> {
    val future = CompletableFuture<Target.Container>()
    this.newFrame(selector).run<Any>().thenAccept {
        future.complete(parseTargetContainer(it, getContext()))
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

fun QuestReader.get(array: Array<String>): ParsedAction<*> {
    return nextOptionalParsedAction(array, null) ?: error("the lack of '${array.map { it }}' cite target")
}

fun QuestReader.nextOptionalParsedActionOrNull(array: Array<out String>): ParsedAction<*>? {
    return try {
        mark()
        expects(*array)
        this.nextParsedAction()
    } catch (e: Exception) {
        reset()
        null
    }
}

fun QuestReader.argumentActionOrNull(array: Array<out String>): ParsedAction<*>? {
    return nextOptionalParsedActionOrNull(array)
}

fun QuestReader.argumentAction(array: Array<out String>, def: Any? = null): ParsedAction<*>? {
    return nextOptionalParsedAction(array, def)
}

// nextOptionalParsedAction
fun QuestReader.nextOptionalParsedAction(array: Array<out String>, def: Any? = null): ParsedAction<*>? {
    return nextOptionalParsedActionOrNull(array) ?: if (def == null) null else literalAction(def)
}

// nextOptionalParsedAction
fun QuestReader.nextOptionalParsedAction(id: String, def: Any? = null): ParsedAction<*>? {
    return nextOptionalParsedActionOrNull(arrayOf(id)) ?: if (def == null) null else literalAction(def)
}

fun QuestReader.nextSelector(): ParsedAction<*> {
    return this.nextSelectorOrNull() ?: error("Selector law")
}

fun QuestReader.nextSelectorOrNull(): ParsedAction<*>? {
    return this.nextOptionalParsedActionOrNull(arrayOf("they", "the", "at"))
}

fun <T> CompletableFuture<Any?>.material(then: (Material) -> T): CompletableFuture<T> {
    return thenApply {
        val id = it!!.toString().uppercase(Locale.getDefault())
        val material = Material.getMaterial(id) ?: error("Block type '$it' is not supported.")
        then(material)
    }
}

fun <T> CompletableFuture<Any?>.byte(then: (Byte) -> T): CompletableFuture<T> {
    return thenApply {
        then(Coerce.toByte(it))
    }
}

fun <T> CompletableFuture<Any?>.materialOrNull(then: (Material?) -> T): CompletableFuture<T> {
    return thenApply {
        val id = it!!.toString().uppercase(Locale.getDefault())
        if (id == "*") {
            then(null)
        }
        val material = Material.getMaterial(id) ?: error("Block type '$it' is not supported.")
        then(material)
    }
}

fun CompletableFuture<Target.Container>.forEachLocation(block: Location.(index: Int) -> Unit) {
    thenAccept { it.forEachLocation(block) }
}

fun CompletableFuture<Target.Container>.forEachLivingEntity(block: LivingEntity.(index: Int) -> Unit) {
    thenAccept { it.forEachLivingEntity(block) }
}

fun CompletableFuture<Target.Container>.forEachPlayer(block: Player.(index: Int) -> Unit) {
    thenAccept { it.forEachPlayer(block) }
}

fun CompletableFuture<Target.Container>.forEachProxyEntity(block: ProxyEntity.(index: Int) -> Unit) {
    thenAccept { it.forEachProxyEntity(block) }
}

fun CompletableFuture<Target.Container>.forEachEntity(block: Entity.(index: Int) -> Unit) {
    thenAccept { it.forEachEntity(block) }
}

/**
 * 创建空容器 dsl
 */
fun createTargetContainerDSL(block: (result: Target.Container) -> Unit): Target.Container {
    return Target.Container().apply(block)
}