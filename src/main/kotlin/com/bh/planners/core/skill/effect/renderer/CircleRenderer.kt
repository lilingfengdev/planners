package com.bh.planners.core.skill.effect.renderer

import com.bh.planners.core.skill.effect.EffectOption
import com.bh.planners.core.skill.effect.Target

class CircleRenderer(target: Target, container: Target.Container, option: EffectOption) : ArcRenderer(target, container,
    option
) {

    override val EffectOption.angle: Double
        get() = 360.0

}