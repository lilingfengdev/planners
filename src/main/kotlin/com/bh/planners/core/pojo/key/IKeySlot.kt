package com.bh.planners.core.pojo.key

import org.bukkit.entity.Player

interface IKeySlot {

    val key: String

    val name: String

    val groups: List<Int>

    val sort: Long

    val description: List<String>

    fun getGroup(player: Player?) : String

}
