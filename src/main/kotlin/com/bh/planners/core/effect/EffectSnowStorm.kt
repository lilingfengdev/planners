package com.bh.planners.core.effect

import com.bh.planners.core.pojo.Context
import org.bukkit.Bukkit

class EffectSnowStorm : com.bh.planners.core.effect.Effect() {
    override val name: String
        get() = "show storm"


    enum class Type {
        DRAGON_CORE, GERM_PLUGIN, NONE
    }

    val HOOKED = lazy {
        if (Bukkit.getPluginManager().isPluginEnabled("DragonCore")) {
            Type.DRAGON_CORE
        } else if (Bukkit.getPluginManager().isPluginEnabled("GermPlugin")) {
            Type.GERM_PLUGIN
        } else Type.NONE
    }


    override fun sendTo(target: Target?, option: EffectOption, context: Context) {


    }
}