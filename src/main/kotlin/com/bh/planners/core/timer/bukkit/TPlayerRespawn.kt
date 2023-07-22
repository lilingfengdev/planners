package com.bh.planners.core.timer.bukkit

import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.timer.AbstractTimer
import com.bh.planners.core.timer.Template
import org.bukkit.event.player.PlayerRespawnEvent
import taboolib.module.kether.ScriptContext

object TPlayerRespawn : AbstractTimer<PlayerRespawnEvent>() {

    override val name: String
        get() = "player respawn"
    override val eventClazz: Class<PlayerRespawnEvent>
        get() = PlayerRespawnEvent::class.java

    override fun check(e: PlayerRespawnEvent): Target {
        return e.player.toTarget()
    }

    /**
     * isBedSpawn 是否在床上复活
     * respawnLoc 复活位置
     */
    override fun onStart(context: ScriptContext, template: Template, e: PlayerRespawnEvent) {
        super.onStart(context, template, e)
        context.rootFrame().variables()["isBedSpawn"] = e.isBedSpawn
        context.rootFrame().variables()["respawnLoc"] = e.respawnLocation
    }

}