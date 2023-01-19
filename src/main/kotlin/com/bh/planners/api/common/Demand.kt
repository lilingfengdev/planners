package com.bh.planners.api.common

import java.util.Collections

/**
 * @author bkm016
 * @since 2020/11/22 2:51 下午
 */
class Demand(val source: String,val starts : Array<Char> = arrayOf(':')) {

    val namespace: String
    val dataMap = Collections.synchronizedMap(LinkedHashMap<String, MutableList<String>>())
    val children = Collections.synchronizedMap(LinkedHashMap<String, Demand>())

    init {
        var args = source.split(" ")
        if (source[0] != ':') {
            namespace = args[0]
            args = args.subList(1, args.size)
        } else {
            namespace = "EMPTY"
        }
        var dataKey : String? = null
        val dataValues = mutableListOf<String>()
        args.forEachIndexed { index, s ->
            if (s[0] in starts) {
                if (dataKey != null) {
                    put(dataKey!!,dataValues.joinToString(" "))
                }
                dataKey = s.substring(1)
                dataValues.clear()
            }
            // 如果key节点追踪到 则自定定位为该key的值
            else if (dataKey != null) {
                dataValues += s
            }
        }
        if (dataKey != null) {
            put(dataKey!!,dataValues.joinToString(" "))
        }


    }

    private fun List<String>.sub(start: Int, prefix: String, suffix: String): List<String> {
        val subList = this.subList(start + 1, this.size)
        var counter = 0
        var mark = -1
        subList.forEachIndexed { index, it ->
            if (it == prefix) {
                counter++
            }
            if (it == suffix) {
                if (counter > 0) {
                    counter--
                } else {
                    mark = index
                }
            }
        }
        return this.subList(start + 1, start + 1 + mark)
    }

    fun has(key: String) = dataMap.containsKey(key)

    private fun put(key: String, value: String) {
        dataMap.computeIfAbsent(key) { mutableListOf() } += value
    }

    fun get(key: List<String>, def: String? = null): String? {
        return key.firstNotNullOfOrNull { get(it) } ?: def
    }

    fun get(key: String, def: String? = null): String? {
        return get(key, 0, def)
    }

    fun get(key: String, index: Int, def: String? = null): String? {
        return dataMap.computeIfAbsent(key) { mutableListOf() }.getOrNull(index) ?: def
    }

    override fun toString(): String {
        return "Demand(namespace='$namespace', dataMap=$dataMap,children=$children)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Demand) return false
        if (source != other.source) return false
        if (dataMap != other.dataMap) return false
        return true
    }

    override fun hashCode(): Int {
        var result = source.hashCode()
        result = 31 * result + dataMap.hashCode()
        return result
    }

    companion object {

        val cache = Collections.synchronizedMap(mutableMapOf<String, Demand>())

        fun String.toDemand(): Demand {
            return cache.computeIfAbsent(this) { Demand(this) }
        }

    }
}