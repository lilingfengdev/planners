package com.bh.planners.core.skill.effect

import taboolib.common.LifeCycle
import taboolib.common.io.getInstance
import taboolib.common.io.runningClasses
import taboolib.common.platform.Awake

object Effects {

    val effects = mutableMapOf<String, Effect>()
    val parsers = mutableMapOf<Array<String>, EffectParser>()


    val STEP = listOf("step", "s")
    val RADIUS = listOf("radius", "r")
    val ANGLE = listOf("angle", "a")


    fun get(key: String): Effect {
        return effects[key]!!
    }

    @Awake(LifeCycle.LOAD)
    fun load() {
        runningClasses.forEach {
            if (Effect::class.java.isAssignableFrom(it)) {
                if (Effect::class.java.isAssignableFrom(it)) {
                    (it.getInstance()?.get() as? Effect)?.let { effect ->
                        effects[effect.name] = effect
                    }
                }
            }
        }
    }

    val effectKeys: Set<String>
        get() = effects.keys

}
