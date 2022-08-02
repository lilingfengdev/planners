package com.bh.planners.core.effect

import com.bh.planners.core.pojo.Context

abstract class Effect {

    abstract val name: String

    abstract fun sendTo(target: Target?, option: EffectOption, context: Context)

}
