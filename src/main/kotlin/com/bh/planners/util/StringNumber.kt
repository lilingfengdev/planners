package com.bh.planners.util

import taboolib.common5.Coerce

/**
 * @author 坏黑
 * @since 2019-05-29 21:43
 */
class StringNumber {
    var type: NumberType? = null
        private set

    private var number: Number? = null
    var source: String? = null
        private set

    constructor(number: Long) {
        this.number = number
        type = NumberType.INT
    }

    constructor(number: Double) {
        this.number = number
        type = NumberType.DOUBLE
    }

    constructor(source: String?) {
        this.source = source
        try {
            number = this.source!!.toDouble()
            type = if (isInt(number!!.toDouble())) NumberType.INT else NumberType.DOUBLE
        } catch (ignored: Throwable) {
            type = NumberType.STRING
        }
    }

    fun add(v: String?): StringNumber {
        val numberFormat = StringNumber(v)
        if (isNumber() && numberFormat.isNumber()) {
            number = number!!.toDouble() + numberFormat.getNumber()!!.toDouble()
            type = if (isInt(number!!.toDouble())) NumberType.INT else NumberType.DOUBLE
        } else {
            source += numberFormat.source
            type = NumberType.STRING
        }
        return this
    }

    fun subtract(v: String?): StringNumber {
        val numberFormat = StringNumber(v)
        if (isNumber() && numberFormat.isNumber()) {
            number = number!!.toDouble() - numberFormat.getNumber()!!.toDouble()
            type = if (isInt(number!!.toDouble())) NumberType.INT else NumberType.DOUBLE
        }
        return this
    }

    fun isInt(value: Double): Boolean {
        return Coerce.toInteger(value).toDouble() == value
    }

    fun get(): Any {
        return when (type) {
            NumberType.INT -> number!!.toLong()
            NumberType.DOUBLE -> number!!.toDouble()
            else -> source!!
        }
    }

    fun isNumber(): Boolean {
        return type == NumberType.INT || type == NumberType.DOUBLE
    }

    fun getNumber(): Number? {
        return number
    }

    enum class NumberType {
        DOUBLE, INT, STRING
    }

    override fun toString(): String {
        return "StringNumber{" +
                "type=" + type +
                ", number=" + number +
                ", source='" + source + '\'' +
                '}'
    }
}
