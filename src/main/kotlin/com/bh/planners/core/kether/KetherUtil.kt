package com.bh.planners.core.kether

import com.bh.planners.api.common.Demand
import com.bh.planners.core.kether.event.ActionEventParser
import com.bh.planners.core.pojo.Context
import com.bh.planners.core.skill.effect.Target
import com.bh.planners.core.skill.effect.Target.Companion.createContainer
import com.bh.planners.core.skill.effect.Target.Companion.toTarget
import com.bh.planners.core.pojo.Session
import com.bh.planners.core.pojo.player.PlayerJob
import com.bh.planners.util.StringNumber
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.adaptPlayer
import taboolib.common5.Coerce
import taboolib.library.kether.ParsedAction
import taboolib.library.kether.QuestContext
import taboolib.library.kether.QuestReader
import taboolib.module.kether.KetherShell
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.printKetherErrorMessage
import taboolib.platform.type.BukkitPlayer
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
    return getSession().executor
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
    val optional = rootVariables().get<Location>("@Origin")
    if (optional.isPresent) {
        return optional.get().toTarget()
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

fun ScriptFrame.createTargets(selector: ParsedAction<*>): CompletableFuture<Target.Container> {
    val future = CompletableFuture<Target.Container>()
    this.newFrame(selector).run<Any>().thenAccept {
        val demand = Demand(it.toString())
        val container = demand.createContainer(toOriginLocation(), getSession())
        future.complete(container)
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