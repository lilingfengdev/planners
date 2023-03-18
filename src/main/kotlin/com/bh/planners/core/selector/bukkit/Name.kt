package com.bh.planners.core.selector.bukkit

import com.bh.planners.core.effect.Target.Companion.getEntity
import com.bh.planners.core.selector.Selector
import java.util.concurrent.CompletableFuture

// 过滤器 过滤名字不是指定名称的人
object Name : Selector {
    override val names: Array<String>
        get() = arrayOf("name")

    override fun check(data: Selector.Data): CompletableFuture<Void> {
        val name = data.read<String>(0, "__null__")
        data.container.removeIf { (it.getEntity()?.name ?: "__null__") != name }
        return CompletableFuture.completedFuture(null)
    }


}