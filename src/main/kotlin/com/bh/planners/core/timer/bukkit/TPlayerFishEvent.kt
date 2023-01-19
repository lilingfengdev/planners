package com.bh.planners.core.timer.bukkit

import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.timer.AbstractTimer
import com.bh.planners.core.timer.Template
import org.bukkit.event.player.PlayerFishEvent
import taboolib.module.kether.ScriptContext

object TPlayerFishEvent : AbstractTimer<PlayerFishEvent>() {
    override val name: String
        get() = "player fish"
    override val eventClazz: Class<PlayerFishEvent>
        get() = PlayerFishEvent::class.java

    override fun check(e: PlayerFishEvent): Target? {
        return e.player.toTarget()
    }

    /**
     * state 状态
     * BITE
     * Called when there is a bite on the hook and it is ready to be reeled in.
     * CAUGHT_ENTITY
     * When a player has successfully caught an entity.
     * CAUGHT_FISH
     * When a player has successfully caught a fish and is reeling it in.
     * FAILED_ATTEMPT
     * When a player fails to catch anything while fishing usually due to poor aiming or timing.
     * FISHING
     * When a player is fishing, ie casting the line out.
     * IN_GROUND
     * When a bobber is stuck in the ground.
     *
     * entity 钓到的实体，state在CAUGHT_FISH,CAUGHT_ENTITY 才存在
     */
    override fun onStart(context: ScriptContext, template: Template, e: PlayerFishEvent) {
        super.onStart(context, template, e)
        context.rootFrame().variables()["state"] = e.state.name
        context.rootFrame().variables()["entity"] = e.caught
    }

}