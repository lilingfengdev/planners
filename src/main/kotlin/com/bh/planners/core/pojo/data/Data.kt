package com.bh.planners.core.pojo.data

import taboolib.common.util.asList
import taboolib.common5.Coerce

/**
 * Chemdah
 * com.bh.planners.core.pojo.data.Data
 *
 * @author sky
 * @since 2021/3/2 12:00 上午
 */
open class Data(
    val data: Any,
    val createStamp: Long = System.currentTimeMillis(),
    var survivalStamp: Long = 0L
) {

    var changed = false

    val isClosed: Boolean
        get() = System.currentTimeMillis() > createStamp + survivalStamp

    val isOpened: Boolean
        get() = !isClosed

    fun toInt(): Int {
        return Coerce.toInteger(data)
    }

    fun toFloat(): Float {
        return Coerce.toFloat(data)
    }

    fun toDouble(): Double {
        return Coerce.toDouble(data)
    }

    fun toLong(): Long {
        return Coerce.toLong(data)
    }

    fun toShort(): Short {
        return Coerce.toShort(data)
    }

    fun toByte(): Byte {
        return Coerce.toByte(data)
    }

    fun toBoolean(): Boolean {
        return Coerce.toBoolean(data)
    }

    fun asList(): List<String> {
        return data.asList()
    }

    override fun toString(): String {
        if (data is List<*> && data.size == 1) {
            return data[0].toString()
        }
        return data.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Data) return false
        if (data != other.data) return false
        return true
    }

    override fun hashCode(): Int {
        return data.hashCode()
    }

    companion object {
        fun unsafeData(any: Any, survivalStamp: Long = -1L) = Data(any, survivalStamp)
    }
}
