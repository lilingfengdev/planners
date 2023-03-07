package com.bh.planners.core.feature.damageable

import org.bukkit.entity.LivingEntity

class DamageableMeta(val context: Damageable, val stream: DamageableModel.Stream) {

    val type = stream.type

    var sender = if (type == DamageableModel.Type.ATTACK) context.attacker else context.victim

    var data: Any? = null

    var cancelStream = false

}