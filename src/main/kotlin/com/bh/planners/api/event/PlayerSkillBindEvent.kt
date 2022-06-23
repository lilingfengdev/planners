package com.bh.planners.api.event

import com.bh.planners.core.pojo.key.IKeySlot
import com.bh.planners.core.pojo.player.PlayerJob
import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

class PlayerSkillBindEvent(val player: Player, val skill: PlayerJob.Skill, val keySlot: IKeySlot, val type: Type) :
    BukkitProxyEvent() {

    enum class Type {
        INSTANCE, UN_INSTANCE
    }

}