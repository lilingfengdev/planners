package com.bh.planners.core.selector.loop

import com.bh.planners.core.effect.Target
import com.bh.planners.core.kether.catchRunning
import com.bh.planners.core.kether.rootVariables
import com.bh.planners.core.pojo.Context
import com.bh.planners.core.selector.Selector
import java.util.concurrent.CompletableFuture

/**
 * 合并目标容器
 * @fetch t0
 */
object Fetch : Selector {

    override val names: Array<String>
        get() = arrayOf("get", "fetch")

    var EMPTY = Target.Container()

    fun Context.getTargetContainer(id: String): Target.Container {
        return ketherScriptContext?.rootFrame()?.rootVariables()?.get<Target.Container>(id)?.orElseGet { EMPTY }
            ?: EMPTY
    }

    override fun check(data: Selector.Data): CompletableFuture<Void> {
        catchRunning {
            data.container += data.context.getTargetContainer(data.read(0, "")).map { target -> target }
        }
        return CompletableFuture.completedFuture(null)
    }
}