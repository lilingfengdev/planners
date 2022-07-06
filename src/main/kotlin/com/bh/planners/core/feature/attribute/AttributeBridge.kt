package com.bh.planners.core.feature.attribute

import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.function.info
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