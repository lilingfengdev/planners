package com.bh.planners.api.common

import taboolib.common.platform.function.submitAsync
import taboolib.common5.Coerce

abstract class ParticleBuilder(open val task: ParticleBuilder.() -> Unit) : Runnable {

    var closed = false

    open fun hasNext(): Boolean {
        return closed
    }

    companion object {

        fun create(time: String, task: ParticleBuilder.() -> Unit): ParticleBuilder {

            if (time.endsWith("f")) {
                return Frame(Coerce.toLong(time.substring(0, time.lastIndex)), task)
            } else if (time.endsWith("t")) {
                return BukkitTick(Coerce.toLong(time.substring(0, time.lastIndex)), task)
            }

            return BukkitTick(Coerce.toLong(time), task)
        }

    }

    class Frame(val duration: Long, task: ParticleBuilder.() -> Unit) : ParticleBuilder(task) {

        var count = 0L

        override fun hasNext(): Boolean {
            count++
            if (count % duration == 0L) {
                count = 0
                return super.hasNext()
            } else return false
        }

        override fun run() {
            submitAsync {
                while (hasNext()) {
                    task()
                }
            }
        }
    }

    class BukkitTick(val tick: Long, override val task: ParticleBuilder.() -> Unit) : ParticleBuilder(task) {

        override fun run() {
            submitAsync(period = tick) {
                if (hasNext()) {
                    task()
                }
            }
        }

    }


}