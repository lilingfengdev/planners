package com.bh.planners.api

import com.bh.planners.api.PlannersAPI.plannersProfileIsLoaded
import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
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

    // 创建玩家代理
    fun createProxy(player: Player): ProxyPlayer {
        player.inventory.addItem()
        return adaptPlayer(player)
    }

    fun create(target: Target, skill: Skill, level: Int = 1): Context.Impl1 {
        return Context.Impl1(target, skill, level)
    }

    fun create(player: Player, skill: PlayerJob.Skill): Context.Impl1 {
        return create(player, skill.instance, skill.level)!!
    }

    // 创建释放上下文
    fun create(player: Player): Context.Impl0 {
        return Context.Impl0(player.toTarget())
    }

    // 创建释放上下文
    fun create(player: Player, skill: String, level: Int): Context.Impl1? {
        val instance = PlannersAPI.getSkill(skill) ?: error("Skill '$skill' not found")
        return create(player, instance, level)
    }

    // 创建释放上下文
    fun create(player: Player, skill: Skill, level: Int): Context.Impl1? {
        if (player.plannersProfileIsLoaded) {
            return Context.Impl1(player.toTarget(), skill, level)
        }
        return null
    }

    fun createSession(target: Target, skill: Skill, consume: Consumer<Session>? = null): Session {
        return Session(target, skill).also { consume?.accept(it) }
    }

    // 创建释放环境 Session
    fun createSession(player: Player, skill: Skill, consume: Consumer<Session>? = null): Session {
        return createSession(player.toTarget(), skill, consume)
    }


}