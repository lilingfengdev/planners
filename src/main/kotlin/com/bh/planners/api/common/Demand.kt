package com.bh.planners.api.common

import taboolib.common.platform.function.info

/**
 * @author bkm016
 * @since 2020/11/22 2:51 下午
 */
class Demand(val source: String) {

    val namespace: String
    val dataMap = LinkedHashMap<String, String>()
    val args = mutableListOf<String>()

    init {
        val args = source.split(" ")
        namespace = args[0]
        val skipIndex = arrayListOf<Int>()
        args.forEachIndexed { index, s ->
            if (index in skipIndex) return@forEachIndexed
            if (s[0] == '-') {
                when {
                    index + 1 >= args.size -> {
                        dataMap[s.substring(1)] = ""
                    }
                    s[1] == '!' && args[index + 1][0] == '-' -> {
                        dataMap[s.substring(2)] = args[index + 1]
                        skipIndex += index + 1
                    }
                    args[index + 1][0] != '-' -> {
                        dataMap[s.substring(1)] = args[index + 1]
                        skipIndex += index + 1
                    }

                    else -> {
                        dataMap[s.substring(1)] = ""
                    }
                }
            } else {
                this.args += s
            }
        }

    }

    fun has(key: String) = dataMap.containsKey(key)

    fun get(key: List<String>, def: String? = null): String? {
        return key.mapNotNull { get(it) }.firstOrNull() ?: def
    }

    fun get(key: String, def: String? = null): String? {
        return dataMap[key] ?: def
    }

    fun get(index: Int, def: String? = null): String? {
        return args.getOrNull(index) ?: def
    }

    fun set(key: String, value: String) {
        dataMap[key] = ""
    }

    override fun toString(): String {
        return "Demand(source='$source', namespace='$namespace', dataMap=$dataMap, args=$args)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Demand) return false
        if (source != other.source) return false
        if (namespace != other.namespace) return false
        if (dataMap != other.dataMap) return false
        if (args != other.args) return false
        return true
    }

    override fun hashCode(): Int {
        var result = source.hashCode()
        result = 31 * result + namespace.hashCode()
        result = 31 * result + dataMap.hashCode()
        result = 31 * result + args.hashCode()
        return result
    }

    companion object {

        fun String.toDemand() = Demand(this)

    }
}