package com.bh.planners.api.common

import taboolib.common.platform.function.info
import java.util.concurrent.CompletableFuture

class CompletableQueueFuture : CompletableFuture<Void>() {

    val table = ArrayList<Data>()

    fun add(name: String, future: CompletableFuture<*>) {
        table += Data(name, future)
    }

    fun process(cur: Int) {
        table[cur].let { data ->
            data.future.thenAccept {
                if (cur < table.size) {
                    process(cur + 1)
                } else {
                    complete(null)
                }
            }
        }
    }

    fun check(): CompletableQueueFuture {
        if (table.isEmpty()) {
            complete(null)
        } else {
            process(0)
        }
        return this
    }

    class Data(val name: String, val future: CompletableFuture<*>) {

    }

}