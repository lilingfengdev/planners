package com.bh.planners.core.timer

import com.bh.planners.core.pojo.Context
import com.bh.planners.core.pojo.Session
import com.bh.planners.core.pojo.Skill
import taboolib.common.platform.ProxyCommandSender
import taboolib.module.configuration.Configuration

class TimerSession(executor: ProxyCommandSender) : Session(executor, EMPTY) {

    companion object {
        val EMPTY = Skill.Empty()
    }

}