package com.bh.planners.core.pojo.key

interface IKeySlot {

    val key: String

    val name: String

    val group: String

    val groups: List<Int>

    val sort: Long

    val description: List<String>

}
