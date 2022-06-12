package com.bh.planners.core.kether.effect.renderer

import com.bh.planners.api.particle.EffectOption
import com.bh.planners.core.kether.effect.Target
import org.bukkit.entity.LivingEntity

interface EffectRenderer {

    fun sendTo(): Set<LivingEntity>

}