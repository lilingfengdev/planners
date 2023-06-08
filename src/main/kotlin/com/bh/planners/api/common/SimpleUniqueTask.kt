package com.bh.planners.api.common

import taboolib.common.platform.function.submitAsync

class SimpleUniqueTask(val id: Any, val tick: Long, val closed: () -> Unit = {}) {

    val task = submitAsync(delay = tick) {
        closed()
    }

    companion object {

        val task = mutableMapOf<Any, SimpleUniqueTask>()

        fun remove(id: Any) {
            task.remove(id)?.task?.cancel()
        }

        fun submit(id: Any, tick: Long, closed: () -> Unit) {
            remove(id)
            task[id] = SimpleUniqueTask(id, tick, closed)
        }

    }

}