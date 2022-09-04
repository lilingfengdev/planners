package com.bh.planners.core.timer.impl

import com.bh.planners.core.timer.AbstractTimer
import com.bh.planners.core.timer.Template
import org.bukkit.event.player.PlayerRespawnEvent
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.kether.ScriptContext

object TimerPlayerRespawn : AbstractTimer<PlayerRespawnEvent>() {
    override val name: String
        get() = "player respawn"
    override val eventClazz: Class<PlayerRespawnEvent>
        get() = PlayerRespawnEvent::class.java

    override fun check(e: PlayerRespawnEvent): ProxyCommandSender? {
        return adaptPlayer(e.player)
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