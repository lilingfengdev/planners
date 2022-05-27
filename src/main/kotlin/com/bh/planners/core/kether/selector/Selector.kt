package com.bh.planners.core.kether.selector

import com.bh.planners.api.particle.Demand
import com.bh.planners.api.particle.EffectOption
import com.bh.planners.core.kether.effect.Target
import com.bh.planners.core.pojo.Session
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

        fun check(sender: Player, session: Session, option: EffectOption, container: Target.Container) {
            check(sender, session, option.demand, container)
        }

        fun check(sender: Player, session: Session, demand: Demand, container: Target.Container) {
            demand.dataMap.keys.filter { it.startsWith('@') }.forEach {
                val selector = getSelector(it.substring(1))
                selector.check(demand.get(it)!!,session, sender, container)
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

    fun check(args: String,session : Session, sender: Player, container: Target.Container)

}
