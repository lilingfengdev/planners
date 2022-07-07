package com.bh.planners.core.timer.impl

import com.bh.planners.core.timer.AbstractTimer
import com.bh.planners.core.timer.Template
import org.bukkit.event.player.PlayerFishEvent
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.kether.ScriptContext

class TimerPlayerFishEvent : AbstractTimer<PlayerFishEvent>() {
    override val name: String
        get() = "player fish"
    override val eventClazz: Class<PlayerFishEvent>
        get() = PlayerFishEvent::class.java

    override fun check(e: PlayerFishEvent): ProxyCommandSender? {
        return adaptPlayer(e.player)
    }

    /**
     * state 状态 FISHING, CAUGHT_FISH, CAUGHT_ENTITY, IN_GROUND, FAILED_ATTEMPT, REEL_IN, BITE
     * entity 钓到的实体，state在CAUGHT_FISH,CAUGHT_ENTITY 才存在
     */
    override fun onStart(context: ScriptContext, template: Template, e: PlayerFishEvent) {
        super.onStart(context, template, e)
        context.rootFrame().variables()["state"] = e.state.name
        context.rootFrame().variables()["entity"] = e.caught
    }

}