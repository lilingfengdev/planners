package com.bh.planners.core.module.team

import com.bh.planners.api.common.Plugin
import com.bh.planners.core.module.ModuleLoader
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.inject.ClassVisitor
import taboolib.common.platform.Awake
import taboolib.library.reflex.Reflex.Companion.invokeConstructor
import taboolib.module.configuration.ConfigNode
import java.util.function.Supplier

object Team : ClassVisitor(0) {

    @ConfigNode("adapter.team")
    private val loader = "__NULL__"

    private val loaderClassRegistry = mutableMapOf<String, Class<Adapter>>()

    var INSTANCE: Adapter? = null

    @Awake(LifeCycle.ENABLE)
    fun load() {
        val adapterClass = loaderClassRegistry[loader]
        if (adapterClass != null) {
            ModuleLoader.selectedModule("Team", loader)
            INSTANCE = adapterClass.invokeConstructor()
        }

    }

    interface Adapter {

        fun getContainer(player: Player): Container?

    }

    interface Container {

        fun getViewers(): List<Player>

        fun isViewer(player: Player): Boolean

    }

    override fun getLifeCycle(): LifeCycle {
        return LifeCycle.ENABLE
    }

    @Suppress("UNCHECKED_CAST")
    override fun visitEnd(clazz: Class<*>, instance: Supplier<*>?) {
        if (Adapter::class.java.isAssignableFrom(clazz)) {
            val annotation = clazz.getAnnotation(Plugin::class.java) ?: return
            if (Bukkit.getPluginManager().isPluginEnabled(annotation.name)) {
                return
            }
            loaderClassRegistry[annotation.name] = clazz as Class<Adapter>
        }
    }

}