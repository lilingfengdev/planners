package com.bh.planners.api

import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.core.kether.namespaces
import com.bh.planners.core.pojo.player.PlayerProfile
import org.bukkit.entity.Player
import taboolib.common.platform.Schedule
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.info
import taboolib.common5.Coerce
import taboolib.module.kether.KetherShell
import taboolib.module.kether.printKetherErrorMessage

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
        return try {
            KetherShell.eval(manaCalculate, sender = adaptPlayer(player), namespace = namespaces)
                .thenApply { Coerce.toDouble(it) }.getNow(0.0)
        } catch (e: Throwable) {
            e.printKetherErrorMessage()
            0.0
        }
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

