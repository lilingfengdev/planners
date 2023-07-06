package com.bh.planners.api

import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.event.PlayerInitializeEvent
import com.bh.planners.api.event.PlayerLevelChangeEvent
import com.bh.planners.api.event.PlayerSelectedJobEvent
import com.bh.planners.api.script.ScriptLoader
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.pojo.Context
import com.bh.planners.core.pojo.Job
import com.bh.planners.core.pojo.Router
import com.bh.planners.core.pojo.player.PlayerProfile
import com.bh.planners.core.storage.Storage
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.common5.Coerce
import taboolib.module.kether.runKether

object Assembly {

    val Job.upgradePoints: String?
        get() = config.getString("upgrade-points")

    val Router.upgradePoints: String?
        get() = config.getString("upgrade-points")


    @SubscribeEvent
    fun e(e: PlayerJoinEvent) {
        loadProfile(e.player)
    }

    fun loadProfile(player: Player) {
        val profile = Storage.INSTANCE.loadProfile(player)

        // 初始化职业
        Storage.INSTANCE.getCurrentJobId(player)?.let { jobId ->
            if (jobId != 0L) {
                profile.job = Storage.INSTANCE.getJob(player, jobId)
            }
        }
        // 初始化flag容器
        profile.flags.merge(Storage.INSTANCE.getDataContainer(player))

        PlannersAPI.profiles[player.uniqueId] = profile
        PlayerInitializeEvent(player, profile).call()
    }

    @Awake(LifeCycle.ENABLE)
    fun autoSave() {
        submit(async = true, period = PlannersOption.autoSaveFlagPeriod, delay = PlannersOption.autoSaveFlagPeriod) {
            saveAll()
        }
    }

    @SubscribeEvent
    fun e(e: PlayerQuitEvent) {
        save(e.player)
        PlannersAPI.profiles.remove(e.player.uniqueId)
    }

    @Awake(LifeCycle.DISABLE)
    fun saveAll() {
        Bukkit.getOnlinePlayers().forEach { save(it) }
    }

    fun save(player: Player) {
        Storage.INSTANCE.update(player.plannersProfile)
    }

    @SubscribeEvent
    fun e(e: PlayerSelectedJobEvent) {
        if (e.profile.hasJob) {
            e.profile.getSkills().filter { it.level == 0 && it.instance.option.isNatural }.forEach {
                it.level = it.instance.option.naturalLevel
                Storage.INSTANCE.updateSkill(e.profile, it)
            }
        }
    }

    @SubscribeEvent
    fun e(e: PlayerLevelChangeEvent) {
        if (e.isUpgraded) {
            val i = e.to - e.from
            val profile = e.player.plannersProfile
            val nextUpgradeGetPoints = nextUpgradeGetPoints(profile, i)
            if (nextUpgradeGetPoints > 0) {
                profile.addPoint(nextUpgradeGetPoints)
            }
        }
    }

    fun nextUpgradeGetPoints(profile: PlayerProfile, value: Int): Int {
        if (profile.hasJob) {
            val expression = profile.job!!.optUpgradePoints ?: return 0
            return runKether {
                Coerce.toInteger(ScriptLoader.createScript(Context.Impl0(profile.player.toTarget()), expression) {
                    this.rootFrame().variables()["value"] = value
                }.get())
            } ?: 0
        }
        return 0
    }


}