package com.bh.planners.core.timer

import com.bh.planners.core.pojo.Session
import com.bh.planners.core.pojo.Skill
import org.bukkit.entity.Player
import taboolib.module.configuration.Configuration

class TimerSession(executor: Player) : Session(executor, EMPTY) {

    companion object {
        val EMPTY = Skill.Empty()
    }

}