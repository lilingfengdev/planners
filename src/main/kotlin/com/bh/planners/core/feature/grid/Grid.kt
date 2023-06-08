package com.bh.planners.core.feature.grid

import com.bh.planners.core.pojo.key.IKeySlot
import taboolib.common5.Coerce

enum class Grid(val id: Int, val slot: Int) {

    MINECRAFT_1(1, 0),
    MINECRAFT_2(2, 1),
    MINECRAFT_3(3, 2),
    MINECRAFT_4(4, 3),
    MINECRAFT_5(5, 4),
    MINECRAFT_6(6, 5),
    MINECRAFT_7(7, 6),
    MINECRAFT_8(8, 7),
    MINECRAFT_9(9, 8);

    fun group() = "minecraft $id"

    companion object {

        fun get(id: Int): Grid {
            return values().firstOrNull { it.id == id } ?: error("Grid minecraft $id not found")
        }

        fun getBySlot(slot: Int): Grid? {
            return values().firstOrNull { it.slot == slot }
        }

        fun isGrid(keySlot: IKeySlot): Boolean {
            val split = keySlot.getGroup(null).split(" ")
            return split.size == 2 && split[0] == "minecraft"
        }


        fun get(keySlot: IKeySlot) = get(keySlot.getGroup(null))

        fun get(group: String): Grid? {
            val split = group.split(" ")
            if (split.size == 2 && split[0] == "minecraft") {
                return get(Coerce.toInteger(split[1]))
            }
            return null
        }
    }

}