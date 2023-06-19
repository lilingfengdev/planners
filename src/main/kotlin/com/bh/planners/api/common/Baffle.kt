package com.bh.planners.api.common

import kotlin.math.max

open class Baffle(val name: Any, var millis: Long) {

    var mark = System.currentTimeMillis()

    val nestMillis = mark + millis

    // 当前时间 大于 结束时间
    val next: Boolean
        get() = countdown == 0L

    private val currentTimeMillis: Long
        get() = System.currentTimeMillis()

    val countdown: Long
        get() = max(nestMillis - currentTimeMillis, 0)

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

        return name == other.name
    }
}