package com.bh.planners.core.kether

import com.bh.planners.core.pojo.Session
import com.bh.planners.util.StringNumber
import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.kether.KetherShell
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.printKetherErrorMessage

const val NAMESPACE = "Planners"

val namespaces = listOf(NAMESPACE)


fun ScriptFrame.getSession(): Session {
    return variables().get<Session>("@PlannersSession").orElse(null) ?: error("Error running environment !")
}


fun Any?.increaseAny(any: Any): Any {
    this ?: return any
    return StringNumber(toString()).add(any.toString()).get()
}

fun evalKether(player: Player, action: String): String? {
    return try {
        KetherShell.eval(action, sender = adaptPlayer(player), namespace = namespaces).get()?.toString()
    } catch (e: Throwable) {
        e.printKetherErrorMessage()
        return null
    }
}