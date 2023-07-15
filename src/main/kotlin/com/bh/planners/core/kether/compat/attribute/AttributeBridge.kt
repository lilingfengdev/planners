package com.bh.planners.core.kether.compat.attribute

import com.bh.planners.api.ContextAPI
import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.event.PlayerInitializeEvent
import com.bh.planners.api.event.PlayerLevelChangeEvent
import com.bh.planners.api.event.PlayerSkillUpgradeEvent
import com.bh.planners.api.event.PluginReloadEvent
import com.bh.planners.api.script.ScriptLoader
import com.bh.planners.core.pojo.player.PlayerJob
import com.bh.planners.core.pojo.player.PlayerProfile
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.info
import taboolib.module.kether.printKetherErrorMessage
import taboolib.module.kether.runKether
import java.util.*

interface AttributeBridge {

    companion object {

        val inspects = mutableListOf(
            Inspect(arrayOf("SX-Attribute"), SXAttributeBridge::class.java) { isEnable },
            Inspect(arrayOf("AttributePlus@3"), AttributePlus3Bridge::class.java) {
                (Bukkit.getPluginManager().getPlugin("AttributePlus")?.description?.version?.split(".")?.get(0)
                    ?: "-1") == "3"
            },
            Inspect(arrayOf("OriginAttribute"), OriginAttributeBridge::class.java) { isEnable },
            Inspect(arrayOf("AttributeSystem"), AttributeSystemBridge::class.java) { isEnable },
            Inspect(arrayOf("MonsterItem"), MonsterItemBridge::class.java) { isEnable },
        )

        val INSTANCE: AttributeBridge? by lazy { createBridge() }

        @Awake(LifeCycle.ENABLE)
        fun createBridge(): AttributeBridge? {
            val inspect = inspects.firstOrNull { it.check(it) } ?: return null
            info("|- Attribute drive lock to [${inspect.names.joinToString(",")}]")
            return inspect.clazz.newInstance()
        }

        @SubscribeEvent
        fun e(e: PlayerInitializeEvent) {
            updateSkill(e.profile)
            updateJob(e.profile)
        }

        @SubscribeEvent
        fun e(e: PluginReloadEvent) {
            Bukkit.getOnlinePlayers().forEach {
                updateSkill(it)
                updateJob(it)
            }
        }

        @SubscribeEvent
        fun e(e: PlayerSkillUpgradeEvent) {
            updateSkill(e.player, e.skill)
            updateJob(e.player)
        }

        @SubscribeEvent
        fun e(e: PlayerLevelChangeEvent) {
            val profile = e.player.plannersProfile
            updateSkill(profile)
            updateJob(profile)
        }

        fun updateSkill(player: Player) {
            updateSkill(player.plannersProfile)
        }

        fun updateSkill(profile: PlayerProfile) {
            profile.getSkills().forEach {
                updateSkill(profile.player, it)
            }
            INSTANCE?.update(profile.player)
        }

        fun updateSkill(player: Player, skill: PlayerJob.Skill) {
            val bridge = INSTANCE ?: return
            val context = ContextAPI.create(player, skill.instance, skill.level)!!
            val attributes = getSkillAttributes(skill)
            try {
                val script = ScriptLoader.createFunctionScript(context, attributes)
                info("skill attribute $script")
                bridge.addAttributes("Skill:${skill.key}", player.uniqueId, -1, script)
            } catch (ex: Exception) {
                ex.printKetherErrorMessage()
            }
        }

        fun updateJob(player: Player) {
            return updateJob(player.plannersProfile)
        }

        fun updateJob(profile: PlayerProfile) {
            val bridge = INSTANCE ?: return
            val context = ContextAPI.create(profile.player)

            runKether {
                val script = ScriptLoader.createFunctionScript(context, getJobAttribute(profile))
                info("job attribute $script")
                bridge.addAttributes("Job", profile.player.uniqueId, -1, script)
                INSTANCE?.update(profile.player)
            }


        }

        fun getSkillAttributes(skill: PlayerJob.Skill): List<String> {
            val attribute = skill.instance.option.attribute
            return attribute.getOrDefaultOrEmpty(skill.level.toString())
        }

        fun getJobAttribute(player: Player): List<String> {
            return getJobAttribute(player.plannersProfile)
        }

        // 优先级 Job > Router
        fun getJobAttribute(profile: PlayerProfile): List<String> {
            val level = profile.level
            val instance = profile.job?.instance ?: return emptyList()

            return instance.option.attribute.getOrDefault("$level")
                ?: instance.router.attribute.getOrDefaultOrEmpty("$level")
        }

    }

    class Inspect(val names: Array<String>, val clazz: Class<out AttributeBridge>, val check: Inspect.() -> Boolean) {

        val isEnable: Boolean
            get() = names.any { Bukkit.getPluginManager().isPluginEnabled(it) }


    }


    fun addAttributes(uuid: UUID, timeout: Long, reads: List<String>)

    fun addAttributes(source: String, uuid: UUID, timeout: Long, reads: List<String>)

    fun removeAttributes(uuid: UUID, source: String)

    fun update(entity: LivingEntity)

    fun update(uuid: UUID)

    fun get(uuid: UUID, keyword: String): Any

    fun get(entity: LivingEntity, keyword: String): Any {
        return get(entity.uniqueId, keyword)
    }

}