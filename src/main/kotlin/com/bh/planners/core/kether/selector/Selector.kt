package com.bh.planners.core.kether.selector

import com.bh.planners.api.particle.Demand
import com.bh.planners.api.particle.EffectOption
import com.bh.planners.core.kether.effect.Target
import com.bh.planners.core.pojo.Session
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

        fun check(target: Target?, session: Session, option: EffectOption, container: Target.Container) {
            check(target, session, option.demand, container)
        }

        fun check(target: Target?, session: Session, demand: Demand, container: Target.Container) {
            demand.dataMap.keys.filter { it.startsWith('@') }.forEach {
                val selector = getSelector(it.substring(1))
                selector.check(target, demand.get(it)!!, session, container)
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

    fun check(target: Target?, args: String, session: Session, container: Target.Container)

}
