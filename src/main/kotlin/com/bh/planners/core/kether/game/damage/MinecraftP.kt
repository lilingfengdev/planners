package com.bh.planners.core.kether.game.damage

import com.bh.planners.api.common.Demand
import org.bukkit.entity.LivingEntity

class MinecraftP : AttackProvider {
    
    override fun process(entity: LivingEntity, damage: Double, source: LivingEntity, demand: Demand) {
        entity.damage(damage, source)
    }
}