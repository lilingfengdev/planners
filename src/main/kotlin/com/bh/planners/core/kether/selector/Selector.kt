package com.bh.planners.core.kether.selector

import com.bh.planners.api.particle.EffectOption
import com.bh.planners.core.kether.effect.Target
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.io.getInstance
import taboolib.common.io.runningClasses
import taboolib.common.platform.Awake

interface Selector {

    companion object {

        val selectors = mutableListOf<Selector>()

        fun getSelector(string: String): Selector {
            return selectors.firstOrNull { string in it.names } ?: error("Selector '${string}' not found")
        }

        fun check(sender: Player, option: EffectOption, container: Target.Container) {
            option.demand.dataMap.keys.filter { it.startsWith('@') }.forEach {
                val selector = getSelector(it.substring(1))
                selector.check(option.demand.get(it)!!, sender, container)
            }
        }

        @Awake(LifeCycle.LOAD)
        fun load() {
            runningClasses.forEach {
                if (Selector::class.java.isAssignableFrom(it)) {
                    (it.getInstance()?.get() as? Selector)?.let { selector ->
                        selectors += selector
                    }
                }
            }
        }

    }

    val names: Array<String>

    fun check(args: String, sender: Player, container: Target.Container)

}
