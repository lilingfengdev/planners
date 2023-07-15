package com.bh.planners.api.event

import com.bh.planners.core.kether.game.damage.DamageType
import org.bukkit.entity.LivingEntity
import taboolib.platform.type.BukkitProxyEvent

class EntityEvents {


    class DamageByEntity(val damager: LivingEntity?, val entity: LivingEntity, var value: Double, val damageType: DamageType) : BukkitProxyEvent()

}