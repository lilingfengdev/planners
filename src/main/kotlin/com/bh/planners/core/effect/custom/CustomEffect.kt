package com.bh.planners.core.effect.custom

import com.bh.planners.core.effect.EffectOption
import com.bh.planners.core.pojo.Context
import taboolib.module.effect.ParticleObj

interface CustomEffect {

    val name: String

    fun getEffectObj(option: EffectOption, context: Context): ParticleObj

}