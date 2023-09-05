package com.bh.planners.api.event

import com.bh.planners.core.kether.game.damage.DamageType
import org.bukkit.entity.LivingEntity
import taboolib.platform.type.BukkitProxyEvent

object EntityEvents {


    class DamageByEntity(val damager: LivingEntity?, val entity: LivingEntity, var value: Double, val damageType: DamageType) : BukkitProxyEvent() {

        // 开放给第三方处理的数据
        val data = mutableMapOf<Any,Any>()

    }

    fun newInstanceDamageByEntity(damager: LivingEntity?,entity: LivingEntity,value: Double,type: DamageType,vars : Map<Any,Any> = emptyMap()): DamageByEntity {
        return DamageByEntity(damager,entity,value,type).also {
            it.data += vars
        }
    }

}