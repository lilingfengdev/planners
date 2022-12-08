package com.bh.planners.api.event.compat

import com.bh.planners.core.pojo.player.PlayerJob
import org.bukkit.entity.LivingEntity
import taboolib.platform.type.BukkitProxyEvent

class PxrpxEvents {


    class Mark(val id: String, val skill: PlayerJob.Skill, val attacker: LivingEntity, val defender: LivingEntity) : BukkitProxyEvent() {

        var amount = 0.0

    }

}