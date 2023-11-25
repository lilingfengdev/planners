package com.bh.planners.core.selector.bukkit

import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.util.createAwaitVoidFuture
import com.bh.planners.core.kether.rootVariables
import com.bh.planners.core.selector.Selector
import java.util.concurrent.CompletableFuture

/**
 * 目标容器
 * @fetch t0
 */
object Fetch : Selector {

    private val EMPTY_CONTAINER = Target.Container()

    override val names: Array<String>
        get() = arrayOf("get", "fetch")

    override fun check(data: Selector.Data): CompletableFuture<Void> {
        val id = data.read<String>(0, "__null__")
        return createAwaitVoidFuture {
            data.context.ketherScriptContext?.rootFrame()
                ?.rootVariables()?.get<Target.Container>(id)?.orElseGet { EMPTY_CONTAINER }?.forEach {
                data.container.add(it)
            }
        }
    }

}