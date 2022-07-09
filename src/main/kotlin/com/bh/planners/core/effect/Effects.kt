package com.bh.planners.core.effect

import taboolib.common.LifeCycle
import taboolib.common.io.getInstance
import taboolib.common.io.runningClasses
import taboolib.common.platform.Awake

object Effects {

    val effects = mutableMapOf<String, com.bh.planners.core.effect.Effect>()
    val parsers = mutableMapOf<Array<String>, EffectParser>()


    val STEP = listOf("step", "s")
    val RADIUS = listOf("radius", "r")
    val ANGLE = listOf("angle", "a")


    fun get(key: String): com.bh.planners.core.effect.Effect {
        return effects[key]!!
    }

    @Awake(LifeCycle.LOAD)
    fun load() {
        runningClasses.forEach {
            if (com.bh.planners.core.effect.Effect::class.java.isAssignableFrom(it)) {
                if (com.bh.planners.core.effect.Effect::class.java.isAssignableFrom(it)) {
                    (it.getInstance()?.get() as? com.bh.planners.core.effect.Effect)?.let { effect ->
                        effects[effect.name] = effect
                    }
                }
            }
        }
    }

    val effectKeys: Set<String>
        get() = effects.keys

}
