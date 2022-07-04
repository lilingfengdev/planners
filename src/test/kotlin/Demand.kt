import java.util.Arrays
import java.util.IdentityHashMap

/**
 * @author bkm016
 * @since 2020/11/22 2:51 下午
 */
class Demand(val source: String) {

    val namespace: String
    val dataMap = LinkedHashMap<String, MutableList<String>>()
    val children = LinkedHashMap<String, Demand>()
    val args = mutableListOf<String>()

    init {
        val args = source.split(" ")
        namespace = args[0]
        val skipIndex = arrayListOf<Int>()
        args.forEachIndexed { index, s ->
            if (index in skipIndex || s.isEmpty()) return@forEachIndexed
            if (s[0] == '-') {
                when {
                    index + 1 >= args.size -> {
                        put(s.substring(1), "")
                    }

                    args[index + 1].startsWith("\\-") -> {
                        put(s.substring(1), args[index + 1].substring(1))
                        skipIndex += index + 1
                    }

                    args[index + 1][0] != '-' -> {
                        put(s.substring(1), args[index + 1])
                        skipIndex += index + 1
                    }

                    else -> {
                        put(s.substring(1), "")
                    }
                }
            } else {
                if (args.size != index + 1 && args[index + 1][0] == '[') {
                    val values = args.sub(index + 1, "[", "]")
                    skipIndex += index + 1
                    skipIndex += index + 2 + values.size
                    values.forEachIndexed { valueIndex, s ->
                        skipIndex += index + 2 + valueIndex
                    }
                    children[s] = Demand(values.joinToString(" "))
                } else {
                    this.args += s
                }
            }
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
        return key.mapNotNull { get(it) }.firstOrNull() ?: def
    }

    fun get(key: String, def: String? = null): String? {
        return get(key, 0, def)
    }

    fun get(key: String, index: Int, def: String? = null): String? {
        return dataMap.computeIfAbsent(key) { mutableListOf() }.getOrNull(index) ?: def
    }

    fun get(index: Int, def: String? = null): String? {
        return args.getOrNull(index) ?: def
    }

    override fun toString(): String {
        return "Demand(source='$source', dataMap=$dataMap, args=$args,children=$children)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Demand) return false
        if (source != other.source) return false
        if (dataMap != other.dataMap) return false
        if (args != other.args) return false
        return true
    }

    override fun hashCode(): Int {
        var result = source.hashCode()
        result = 31 * result + dataMap.hashCode()
        result = 31 * result + args.hashCode()
        return result
    }

    companion object {

        val cache = mutableMapOf<String, com.bh.planners.api.common.Demand>()

        fun String.toDemand(): com.bh.planners.api.common.Demand {
            return cache.computeIfAbsent(this) {
                com.bh.planners.api.common.Demand(this)
            }
        }

    }
}