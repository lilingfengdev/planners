package com.bh.planners.api

import org.bukkit.entity.Player
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.function.adaptPlayer

object ContextAPI {

    fun createProxy(player: Player): ProxyPlayer {
        return adaptPlayer(player)
    }

}