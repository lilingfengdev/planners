package com.bh.planners.core.kether.selector

import com.bh.planners.api.common.CompletableQueueFuture
import com.bh.planners.api.common.Demand
import com.bh.planners.api.common.Plugin
import com.bh.planners.core.kether.catchRunning
import com.bh.planners.core.pojo.Context
import com.bh.planners.core.effect.EffectOption
import com.bh.planners.core.effect.Target
import org.bukkit.Bukkit
import taboolib.common.LifeCycle
import taboolib.common.io.getInstance
import taboolib.common.io.runningClasses
import taboolib.common.platform.Awake
import taboolib.common.platform.function.info
import java.util.concurrent.CompletableFuture

interface Selector {

    companion object {

        val selectors = mutableListOf<Selector>()

        fun getSelector(string: String): Selector {
            return selectors.firstOrNull { string in it.names } ?: error("Selector '${string}' not found")
        }

        fun check(
            target: Target?, context: Context, option: EffectOption, container: Target.Container
        ): CompletableFuture<Void> {
            return check(target, context, option.demand, container)
        }

        fun check(
            target: Target?, context: Context, demand: Demand, container: Target.Container
        ): CompletableFuture<Void> {
            val future = CompletableQueueFuture()
            val keys = demand.dataMap.filter { it.key[0] == '@' }.keys.toMutableList()

            fun process(cur: Int) {
                val key = keys[cur]
                catchRunning {
                    val namespace = key.substring(1)
                    val futures = demand.dataMap[key]?.map {
                        getSelector(namespace).check(namespace, target, it, context, container)
                    } ?: emptyList()
                    CompletableFuture.allOf(*futures.toTypedArray()).thenAccept {
                        if (cur < keys.size - 1) {
                            process(cur + 1)
                        } else {
                            future.complete(null)
                        }
                    }
                }
            }
            if (keys.isNotEmpty()) process(0)
            return future
        }

        @Awake(LifeCycle.LOAD)
        fun load() {
            runningClasses.forEach {
                if (Selector::class.java.isAssignableFrom(it)) {
                    if (it.isAssignableFrom(Plugin::class.java)) {
                        val annotation = it.getAnnotation(Plugin::class.java)
                        if (!Bukkit.getPluginManager().isPluginEnabled(annotation.name)) {
                            return@forEach
                        }
                    }

                    (it.getInstance()?.get() as? Selector)?.let { selector ->
                        selectors += selector
                    }
                }
            }
        }

    }

    val names: Array<String>

    fun check(
        name: String, target: Target?, args: String, context: Context, container: Target.Container
    ): CompletableFuture<Void>

    fun String.isNon(): Boolean {
        return get(0) == '!'
    }

}
