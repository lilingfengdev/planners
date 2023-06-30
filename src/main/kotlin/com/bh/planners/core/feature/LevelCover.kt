package com.bh.planners.core.feature

import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.PlannersAPI.plannersProfileIsLoaded
import com.bh.planners.api.PlannersOption
import com.bh.planners.api.addExperience
import com.bh.planners.api.event.PlayerGetExperienceEvent
import com.bh.planners.api.event.PlayerInitializeEvent
import com.bh.planners.api.event.PlayerLevelChangeEvent
import com.bh.planners.api.event.PlayerSelectedJobEvent
import com.bh.planners.api.hasJob
import com.bh.planners.core.pojo.level.Level
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerExpChangeEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common5.Coerce

object LevelCover {

    val minecraftCover: Boolean
        get() = PlannersOption.root.getBoolean("level-cover")

    val minecraftExpAbsorption: Boolean
        get() = PlannersOption.root.getBoolean("minecraft-exp-absorption", false)

    @SubscribeEvent
    fun e(e: PlayerExpChangeEvent) {

        if (minecraftExpAbsorption && e.amount > 0 && e.player.plannersProfileIsLoaded && e.player.hasJob) {
            e.player.plannersProfile.addExperience(e.amount)
            e.amount = 0
        }
    }

    @SubscribeEvent
    fun e(e: PlayerLevelChangeEvent) {
        if (minecraftCover) {
            update(e.player)
        }
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun e(e: PlayerGetExperienceEvent) {
        if (minecraftCover) {
            update(e.player)
        }
    }

    @SubscribeEvent
    fun e(e: PlayerInitializeEvent) {
        if (minecraftCover) {
            update(e.player)
        }
    }

    @SubscribeEvent
    fun e(e: PlayerSelectedJobEvent) {
        if (minecraftCover) {
            update(e.profile.player)
        }
    }

    fun update(player: Player) {
        if (player.plannersProfileIsLoaded && player.hasJob) {
            update(player, player.plannersProfile.job?.counter!!)
        }

    }

    fun update(player: Player, level: Level) {
        player.sendMessage()
        player.level = level.level
        if (level.top == Int.MAX_VALUE || level.experience / level.top >= 1.0) {
            player.exp = 1f
        } else {
            player.exp = Coerce.toFloat(level.experience / level.top)
        }
    }


}