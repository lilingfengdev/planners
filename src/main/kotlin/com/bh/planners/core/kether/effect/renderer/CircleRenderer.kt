package com.bh.planners.core.kether.effect.renderer

import com.bh.planners.api.particle.EffectOption
import com.bh.planners.core.kether.effect.Target
import org.bukkit.entity.LivingEntity

class CircleRenderer(target: Target, container: Target.Container, option: EffectOption) : ArcRenderer(target, container,
    option
) {

    override val EffectOption.angle: Double
        get() = 360.0

}