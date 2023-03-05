package com.bh.planners.core.feature.damageable

import com.bh.planners.core.pojo.data.DataContainer
import org.bukkit.entity.LivingEntity

class Damageable(val attacker: LivingEntity, val victim: LivingEntity, val model: DamageableModel) {

    val damageSources = mutableMapOf<Any, DamageSource>()

    val data = DataContainer()

    class DamageSource(val value: Double) {

    }

}