package com.bh.planners.core.feature.damageable

import com.bh.planners.core.pojo.data.DataContainer
import com.bh.planners.util.timing
import org.bukkit.entity.LivingEntity

class Damageable(val attacker: LivingEntity, val victim: LivingEntity, val model: DamageableModel) {

    val damageSources = mutableMapOf<Any, DamageSource>()

    var timing = timing()

    val data = DataContainer()

    var metaCancel: DamageableMeta? = null

    var metaIndex = 0

    val countDamage: Double
        get() = damageSources.values.sumByDouble { it.value }

    val metas = Array<DamageableMeta?>(model.streamSize) { null }

    class DamageSource(var value: Double)

}