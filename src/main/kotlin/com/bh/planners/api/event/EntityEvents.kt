package com.bh.planners.api.event

import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import taboolib.platform.type.BukkitProxyEvent

class EntityEvents {


    class DamageByEntity(val damager: LivingEntity?,val entity: LivingEntity,val value: Double) : BukkitProxyEvent()

}