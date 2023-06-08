package com.bh.planners.api.event

import com.bh.planners.core.pojo.player.PlayerJob
import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

class PlayerSkillUpgradeEvent(
    val player: Player,
    val skill: PlayerJob.Skill,
) : BukkitProxyEvent()