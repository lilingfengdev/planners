package com.bh.planners.api.event

import com.bh.planners.core.pojo.Skill
import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

class PlayerCastSkillEvent(val player: Player, val skill: Skill) : BukkitProxyEvent() {
}