package com.bh.planners.core.timer.bukkit

import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.timer.Template
import com.bh.planners.core.timer.Timer
import org.bukkit.event.player.PlayerToggleSneakEvent
import taboolib.module.kether.ScriptContext

object TPlayerSneak : Timer<PlayerToggleSneakEvent> {
    override val name: String
        get() = "player sneak"

    override val eventClazz: Class<PlayerToggleSneakEvent>
        get() = PlayerToggleSneakEvent::class.java

    override fun check(e: PlayerToggleSneakEvent): Target? {
        return e.player.toTarget()
    }

    /**
     * isSneaking 是否在潜行
     */
    override fun onStart(context: ScriptContext, template: Template, e: PlayerToggleSneakEvent) {
        context.rootFrame().variables()["isSneaking"] = e.isSneaking
    }

}