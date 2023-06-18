package com.bh.planners.core.selector.bukkit

import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.selector.Selector
import org.bukkit.Bukkit
import taboolib.common.platform.function.submit
import java.util.concurrent.CompletableFuture

object Server : Selector {
    override val names: Array<String>
        get() = arrayOf("server")

    override fun check(data: Selector.Data): CompletableFuture<Void> {
        val future = CompletableFuture<Void>()
        submit(async = false) {
            Bukkit.getOnlinePlayers().forEach { data.container += it.toTarget() }
            future.complete(null)
        }
        return future
    }

}