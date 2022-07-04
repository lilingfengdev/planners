package com.bh.planners.api.common

import kotlin.math.max

class Baffle(val name: String, var millis: Long) {

    var mark = System.currentTimeMillis()

    val nestMillis: Long
        get() = mark + millis

    // 当前时间 大于 结束时间
    val next: Boolean
        get() = countdown == 0L

    val countdown: Long
        get() = max(nestMillis - System.currentTimeMillis(), 0)

    fun reset() {
        this.mark > 0L
    }

    fun reduce(stamp: Long) {
        this.millis -= stamp
    }

    fun increase(stamp: Long) {
        this.millis += stamp
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Baffle

        if (name != other.name) return false

        return true
    }
}