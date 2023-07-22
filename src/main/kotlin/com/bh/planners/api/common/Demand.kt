package com.bh.planners.api.common

import java.util.*

/**
 * @author bkm016
 * @since 2020/11/22 2:51 下午
 */
class Demand(val source: String, val starts: Array<Char> = arrayOf(':')) {

    val namespace: String
    val dataMap: MutableMap<String, MutableList<String>> = Collections.synchronizedMap(LinkedHashMap())
    val children: MutableMap<String, Demand> = Collections.synchronizedMap(LinkedHashMap())

    init {
        if (source.isNotEmpty()) {
            var args = source.split(" ")
            if (source.isNotEmpty() && source[0] != ':' && source[0] != '@') {
                namespace = args[0]
                args = args.subList(1, args.size)
            } else {
                namespace = "EMPTY"
            }
            var dataKey: String? = null
            val dataValues = mutableListOf<String>()
            args.forEach { s ->
                // 次要权重 starts
                if (s[0] in starts) {
                    if (dataKey != null) {
                        put(dataKey!!, dataValues.joinToString(" "))
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
                put(dataKey!!, dataValues.joinToString(" "))
            }
        } else {
            namespace = "EMPTY"
        }

    }


    private fun put(key: String, value: String) {
        dataMap.computeIfAbsent(key) { mutableListOf() } += value
    }

    fun get(key: List<String>, def: String? = null): String? {
        key.forEach { theKey ->
            val info = get(theKey, null)
            if (info != null) {
                return info
            }
        }
        return def
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
        return dataMap == other.dataMap
    }

    override fun hashCode(): Int {
        var result = source.hashCode()
        result = 31 * result + dataMap.hashCode()
        return result
    }

    companion object {

        val cache: MutableMap<String, Demand> = Collections.synchronizedMap(mutableMapOf<String, Demand>())

        fun String.toDemand(): Demand {
            return cache.computeIfAbsent(this) { Demand(this) }
        }

    }
}