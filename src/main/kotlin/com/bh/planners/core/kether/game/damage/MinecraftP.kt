package com.bh.planners.core.kether.game.damage

import org.bukkit.entity.LivingEntity

class MinecraftP : AttackProvider {
    override fun doDamage(entity: LivingEntity, damage: Double, source: LivingEntity) {
        entity.damage(damage,source)
    }
}