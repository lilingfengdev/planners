package com.bh.planners.core.effect

import com.bh.planners.core.effect.custom.CustomEffect
import taboolib.common.LifeCycle
import taboolib.common.io.getInstance
import taboolib.common.io.runningClasses
import taboolib.common.platform.Awake

object Effects {

    private val effects = mutableMapOf<String, CustomEffect>()

    fun get(key: String): CustomEffect {
        return effects[key] ?: error("no effect $key")
    }

    @Awake(LifeCycle.LOAD)
    fun load() {
        runningClasses.forEach {
            if (CustomEffect::class.java.isAssignableFrom(it)) {
                (it.getInstance()?.get() as? CustomEffect)?.let { effect ->
                    effects[effect.name] = effect
                }
            }
        }
    }

    fun CustomEffect.register() {
        effects[name] = this
    }

    val effectKeys: Set<String>
        get() = effects.keys

}
