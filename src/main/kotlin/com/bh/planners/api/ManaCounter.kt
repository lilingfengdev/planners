package com.bh.planners.api

import com.bh.planners.api.ManaCounter.addMana
import com.bh.planners.api.PlannersAPI.profile
import com.bh.planners.core.kether.NAMESPACE
import com.bh.planners.core.kether.evalKether
import com.bh.planners.core.kether.namespaces
import com.bh.planners.core.pojo.Skill
import com.bh.planners.core.pojo.player.PlayerProfile
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.platform.Schedule
import taboolib.common.platform.function.adaptPlayer
import taboolib.common5.Coerce
import taboolib.module.kether.KetherShell
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.math.max
import kotlin.math.min

object ManaCounter {

    private val cache = Collections.synchronizedMap(mutableMapOf<UUID, Double>())

    @Schedule(period = 20, async = true)
    fun timer() {
        Bukkit.getOnlinePlayers().forEach { player ->
            calculate(player).thenAccept {
                cache[player.uniqueId] = Coerce.toDouble(it)
            }
        }
    }

    private fun calculate(player: Player): CompletableFuture<Double> {
        val playerJob = player.profile().job ?: return CompletableFuture.completedFuture(0.0)
        val manaCalculate = playerJob.instance.option.manaCalculate
        return KetherShell.eval(manaCalculate, sender = adaptPlayer(player), namespace = namespaces)
            .thenApply { Coerce.toDouble(it) }
    }

    fun get(player: Player): Double {
        if (!cache.containsKey(player.uniqueId)) {
            cache[player.uniqueId] = calculate(player).get()
        }
        return get(player.uniqueId)
    }

    fun get(uuid: UUID): Double {
        return cache[uuid]!!
    }

    fun PlayerProfile.takeMana(skill: Skill) {
        val double = Coerce.toDouble(evalKether(player, skill.option.mpCost))
        takeMana(double)
    }

    fun PlayerProfile.takeMana(value: Double) {
        this.addMana(-value)
    }

    fun PlayerProfile.addMana(value: Double) {
        val result = max(min(player.toCurrentMana() + value, 0.0), player.toMaxMana())
        this.setMana(result)
    }

    fun PlayerProfile.setMana(value: Double) {
        this.mana = value
    }

    fun Player.toCurrentMana(): Double {
        return profile().mana
    }

    fun Player.toMaxMana(): Double {
        return get(this)
    }

}

