package com.bh.planners.api

import com.bh.planners.api.PlannersAPI.plannersProfileIsLoaded
import com.bh.planners.core.kether.game.ActionSkillCast
import com.bh.planners.core.pojo.Context
import com.bh.planners.core.pojo.Session
import com.bh.planners.core.pojo.Skill
import com.bh.planners.core.pojo.player.PlayerJob
import org.bukkit.entity.Player
import org.bukkit.util.Consumer
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.function.adaptPlayer

object ContextAPI {

    fun createProxy(player: Player): ProxyPlayer {
        return adaptPlayer(player)
    }

    fun create(player: Player, skill: String, level: Int): Context.Impl1? {
        val instance = PlannersAPI.getSkill(skill) ?: error("Skill '$skill' not found")
        return create(player, instance, level)
    }

    fun create(player: Player, skill: Skill, level: Int): Context.Impl1? {
        if (player.plannersProfileIsLoaded) {
            return Context.Impl1(createProxy(player), skill, level)
        }
        return null
    }

    /**
     * 创建释放环境 Session
     */
    fun createSession(player: Player, skill: Skill, consume: Consumer<Session>? = null): Session {
        return Session(createProxy(player), skill).also { consume?.accept(it) }
    }


}