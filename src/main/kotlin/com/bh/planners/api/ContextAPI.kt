package com.bh.planners.api

import com.bh.planners.api.PlannersAPI.plannersProfileIsLoaded
import com.bh.planners.core.kether.game.ActionSkillCast
import com.bh.planners.core.pojo.player.PlayerJob
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.function.adaptPlayer

object ContextAPI {

    fun createProxy(player: Player): ProxyPlayer {
        return adaptPlayer(player)
    }

    fun cast(player: Player, skill: String, level: Int) {
        if (player.plannersProfileIsLoaded) {
            val instance = PlannersAPI.getSkill(skill)!!
            ActionSkillCast.ContextImpl(createProxy(player), instance, level).cast()
        }
    }


}