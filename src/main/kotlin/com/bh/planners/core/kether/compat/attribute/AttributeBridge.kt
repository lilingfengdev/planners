package com.bh.planners.core.kether.compat.attribute

import com.bh.planners.api.ContextAPI
import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.event.PlayerInitializeEvent
import com.bh.planners.api.event.PlayerSkillUpgradeEvent
import com.bh.planners.api.event.PluginReloadEvent
import com.bh.planners.core.kether.namespaces
import com.bh.planners.core.kether.rootVariables
import com.bh.planners.core.pojo.player.PlayerJob
import com.bh.planners.core.pojo.player.PlayerProfile
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.info
import taboolib.module.kether.KetherFunction
import taboolib.module.kether.runKether
import java.util.UUID

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
        }

        @SubscribeEvent
        fun e(e: PluginReloadEvent) {
            Bukkit.getOnlinePlayers().forEach {
                updateSkill(it)
            }
        }

        @SubscribeEvent
        fun e(e: PlayerSkillUpgradeEvent) {
            updateSkill(e.player, e.skill)
            INSTANCE?.update(e.player)
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
            val context = ContextAPI.create(player, skill.instance, skill.level)


            runKether {
                val strings =
                    KetherFunction.parse(
                        getSkillAttributes(skill),
                        namespace = namespaces,
                        sender = adaptPlayer(player)
                    ) {
                        rootFrame().rootVariables()["@Context"] = context
                    }
                bridge.addAttributes("Skill:${skill.key}", player.uniqueId, -1, strings)
            }
        }

        fun getSkillAttributes(skill: PlayerJob.Skill): List<String> {
            val attribute = skill.instance.option.attribute

            return attribute.get(skill.level.toString()) ?: attribute.default
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

}