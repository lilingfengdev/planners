package com.bh.planners.core.kether.game.damage

import com.bh.planners.core.kether.compat.attribute.*
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.function.info

interface AttackProvider {

    fun doDamage(entity: LivingEntity, damage: Double, source: LivingEntity)

    companion object {

        val inspects = mutableListOf(
            Inspect(arrayOf("OriginAttribute"), OriginP::class.java) { isEnable },
        )

        val MINECRAFT = Inspect(arrayOf("minecraft"), MinecraftP::class.java) { true }

        val INSTANCE: AttackProvider? by lazy { createBridge() }

        @Awake(LifeCycle.ENABLE)
        fun createBridge(): AttackProvider? {
            val inspect = inspects.firstOrNull { it.check(it) } ?: MINECRAFT
            return inspect.clazz.newInstance()
        }


    }

    class Inspect(val names: Array<String>, val clazz: Class<out AttackProvider>, val check: Inspect.() -> Boolean) {

        val isEnable: Boolean
            get() = names.any { Bukkit.getPluginManager().isPluginEnabled(it) }

    }

}