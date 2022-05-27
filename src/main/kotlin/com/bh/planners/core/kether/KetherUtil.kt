package com.bh.planners.core.kether

import com.bh.planners.core.pojo.Session
import com.bh.planners.core.pojo.player.PlayerJob
import com.bh.planners.util.StringNumber
import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptPlayer
import taboolib.library.kether.QuestContext
import taboolib.module.kether.KetherShell
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.printKetherErrorMessage

const val NAMESPACE = "Planners"

val namespaces = listOf(NAMESPACE)


fun ScriptFrame.getSession(): Session {
    return rootVariables().get<Session>("@Session").orElse(null) ?: error("Error running environment !")
}

fun ScriptFrame.getSkill(): PlayerJob.Skill {
    return rootVariables().get<PlayerJob.Skill>("@Skill").orElse(null) ?: error("Error running environment !")
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