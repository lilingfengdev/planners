package com.bh.planners.core.feature

import com.bh.planners.api.ContextAPI
import com.bh.planners.api.ManaCounter.addMana
import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.PlannersAPI.plannersProfileIsLoaded
import com.bh.planners.api.PlannersOption
import com.bh.planners.api.hasJob
import com.bh.planners.api.script.ScriptLoader
import com.bh.planners.core.kether.namespaces
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.info
import taboolib.common.platform.function.submit
import taboolib.common.platform.service.PlatformExecutor
import taboolib.common5.Coerce
import taboolib.common5.cdouble
import taboolib.module.kether.KetherShell
import taboolib.module.kether.printKetherErrorMessage
import taboolib.module.kether.runKether

object RegainMana {

    var actionbarPlatformTask: PlatformExecutor.PlatformTask? = null

    fun regainManaValue(player: Player): Double {
        val expression = getManaExpression(player) ?: return 0.0
        return runKether(0.0) {
            ScriptLoader.createScript(ContextAPI.create(player), expression).cdouble
        }!!
    }

    fun nextRegainMana(player: Player): Double {
        val value = regainManaValue(player)
        if (value <= 0.0) return value
        val profile = player.plannersProfile
        profile.addMana(value)
        return value
    }

    fun getManaExpression(player: Player): String? {
        if (!player.plannersProfileIsLoaded) return null
        val profile = player.plannersProfile
        if (!profile.hasJob) return null
        val instance = profile.job!!.instance
        return instance.option.regainManaExperience ?: instance.router.regainManaExperience
        ?: PlannersOption.regainManaExperience
    }

    @Awake(LifeCycle.ENABLE)
    fun runTask() {
        actionbarPlatformTask = submit(period = PlannersOption.regainManaPeriod, async = true) {
            Bukkit.getOnlinePlayers().forEach(RegainMana::nextRegainMana)
        }
    }


}