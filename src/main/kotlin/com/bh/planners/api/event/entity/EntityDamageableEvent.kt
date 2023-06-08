package com.bh.planners.api.event.entity

import com.bh.planners.core.feature.damageable.Damageable
import taboolib.platform.type.BukkitProxyEvent

class EntityDamageableEvent(val damageable: Damageable) : BukkitProxyEvent() {

    val attacker = damageable.attacker

    val victim = damageable.victim

}