package com.bh.planners.core.feature

import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.PlannersAPI.plannersProfileIsLoaded
import com.bh.planners.api.PlannersOption
import com.bh.planners.api.addExperience
import com.bh.planners.api.event.PlayerGetExperienceEvent
import com.bh.planners.api.hasJob
import com.bh.planners.core.pojo.level.Level
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerExpChangeEvent
import taboolib.common.platform.event.SubscribeEvent

object LevelCover {

    val isEnable: Boolean
        get() = PlannersOption.root.getBoolean("level-cover")

    @SubscribeEvent
    fun e(e: PlayerExpChangeEvent) {
        if (isEnable) {
            if (e.player.plannersProfileIsLoaded && e.player.hasJob) {
                e.player.plannersProfile.addExperience(e.amount)
            }
            e.amount = 0
        }

    }

    @SubscribeEvent(ignoreCancelled = true)
    fun e(e: PlayerGetExperienceEvent) {
        if (isEnable) {
            update(e.player, e.player.plannersProfile.job!!.counter)
        }
    }


    fun update(player: Player, level: Level) {

        player.level = level.level
        if (level.top == Int.MAX_VALUE) {
            player.exp = 1f
        } else {
            player.exp = level.experience.toFloat() / level.top.coerceAtLeast(0)
        }
    }


}