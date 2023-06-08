package com.bh.planners.api

import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.script.ScriptLoader
import com.bh.planners.core.effect.Target.Companion.target
import com.bh.planners.core.pojo.Context
import com.bh.planners.core.pojo.player.PlayerProfile
import com.bh.planners.util.runKetherThrow
import org.bukkit.entity.Player
import taboolib.common.platform.Schedule
import taboolib.common5.cdouble

object ManaCounter {

    private const val MANA_NAMESPACE = "@Mana"
    private const val MAX_MANA_NAMESPACE = "@MaxMana"

    @Schedule(period = 100, async = true)
    fun timer() {
        PlannersAPI.profiles.values.forEach {
            if (it.player.isOnline) {
                it.updateFlag(MAX_MANA_NAMESPACE, calculate(it.player))
            }
        }
    }

    private fun calculate(player: Player): Double {
        val playerJob = player.plannersProfile.job ?: return 0.0
        val manaCalculate = playerJob.instance.option.manaCalculate
        val context = object : Context.Impl0(player.target()) {
            override var stackId: String = "Job:mana expression"
        }
        return runKetherThrow(context, 0.0) {
            ScriptLoader.createScript(ContextAPI.create(player), manaCalculate).get().cdouble
        }!!
    }

    fun PlayerProfile.takeMana(value: Double) {
        this.addMana(-value)
    }

    fun PlayerProfile.addMana(value: Double) {
        val result = (toCurrentMana() + value).coerceAtLeast(0.0).coerceAtMost(toMaxMana())
        this.setMana(result)
    }

    fun PlayerProfile.setMana(value: Double) {
        this.updateFlag(MANA_NAMESPACE, value)
    }

    fun Player.toCurrentMana(): Double {
        return plannersProfile.toCurrentMana()
    }

    fun PlayerProfile.toCurrentMana(): Double {
        return getFlag(MANA_NAMESPACE)?.toDouble() ?: 0.0
    }

    fun PlayerProfile.toMaxMana(): Double {
        return getFlag(MAX_MANA_NAMESPACE)?.toDouble() ?: calculate(player)
    }

    fun Player.toMaxMana(): Double {
        return plannersProfile.toMaxMana()
    }

}

