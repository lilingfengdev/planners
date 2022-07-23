import com.bh.planners.api.PlannersOption
import com.bh.planners.api.common.Baffle
import java.util.Collections
import java.util.LinkedList

object Emitter {

    val ORDERLY = object : Decider {
        override fun accept(player: String, list: List<Int>): Boolean {
            // 取所需元素
            val baffles = getUsableUnits(player).filter { it.name in list }
            if (baffles.isEmpty()) return false

            // 首位下标
            val first = list[0]
            val indexOf = baffles.indexOfFirst { it.name == first }
            if (indexOf == -1 || baffles.size - indexOf < list.size) return false

            val values = baffles.subList(indexOf, indexOf + list.size)
            if (values.size < baffles.size) return false
            list.forEachIndexed { index, value ->
                if (value != values[index].name) {
                    return false
                }
            }
            return true
        }

    }

    val DISORDERLY = object : Decider {

        override fun accept(player: String, list: List<Int>): Boolean {
            // 取所需元素
            val baffles = getUsableUnits(player).filter { it.name in list }.map { it.name }.toMutableList()
            if (baffles.isEmpty()) return false
            list.forEach {
                val indexOf = baffles.indexOf(it)
                if (indexOf == -1) {
                    return false
                }
                baffles.removeAt(indexOf)
            }
            return true
        }

    }

    val units = Collections.synchronizedMap(mutableMapOf<String, LinkedList<Baffle>>())


    fun tryDecide(player: String, type: Decider, list: List<Int>): Boolean {
        if (list.isEmpty()) return false
        return type.accept(player, list)
    }

    fun getUnits(player: String) = units.computeIfAbsent(player) { LinkedList<Baffle>() }

    fun getUsableUnits(player: String) = getUnits(player).filter { !it.next }

    fun registerUnit(player: String, unit: Baffle) {
        getUnits(player) += unit
    }

    interface Decider {

        fun accept(player: String, list: List<Int>): Boolean

    }

}