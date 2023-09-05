package com.bh.planners.core.kether.game.damage

import com.bh.planners.api.common.Demand
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.library.reflex.Reflex.Companion.invokeConstructor

interface AttackProvider {

    fun process(entity: LivingEntity, damage: Double, source: LivingEntity, demand: Demand)

    companion object {

        val inspects = mutableListOf(
            Inspect(arrayOf("OriginAttribute"), OriginP::class.java) { isEnable },
        )

        val MINECRAFT = Inspect(arrayOf("minecraft"), MinecraftP::class.java) { true }

        val INSTANCE: AttackProvider? by lazy { createBridge() }

        @Awake(LifeCycle.ENABLE)
        fun createBridge(): AttackProvider {
            val inspect = inspects.firstOrNull { it.check(it) } ?: MINECRAFT
            return inspect.clazz.invokeConstructor()
        }


    }

    class Inspect(val names: Array<String>, val clazz: Class<out AttackProvider>, val check: Inspect.() -> Boolean) {

        val isEnable: Boolean
            get() = names.any { Bukkit.getPluginManager().isPluginEnabled(it) }

    }

}