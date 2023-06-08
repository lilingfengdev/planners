package com.bh.planners.core.pojo.key

import org.bukkit.entity.Player
import taboolib.library.configuration.ConfigurationSection

class KeySlot(val config: ConfigurationSection) : IKeySlot {

    override val key = config.name

    private val group = config.getString("group", key)!!

    override val groups: List<Int>
        get() = config.getIntegerList("groups")

    override val name = config.getString("name", key)!!

    override val sort = config.getLong("sort", 1)

    override val description = config.getStringList("description")

    override fun getGroup(player: Player?): String {
        return this.group
    }

}
