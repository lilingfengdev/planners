package com.bh.planners.core.kether.effect

import taboolib.common.LifeCycle
import taboolib.common.io.getInstance
import taboolib.common.io.runningClasses
import taboolib.common.platform.Awake

object Effects {

    val loaders = mutableMapOf<String, EffectLoader<*>>()


    val STEP = listOf("step", "s")
    val RADIUS = listOf("radius", "r")
    val ANGLE = listOf("angle", "a")


    fun get(key: String): EffectLoader<*> {
        return loaders[key]!!
    }

    @Awake(LifeCycle.LOAD)
    fun load() {
        runningClasses.forEach {
            if (EffectLoader::class.java.isAssignableFrom(it)) {
                (it.getInstance()?.get() as? EffectLoader<*>)?.let { loader ->
                    loaders[loader.name] = loader
                }
            }
        }
    }

    val loaderKeys: Set<String>
        get() = loaders.keys

}
