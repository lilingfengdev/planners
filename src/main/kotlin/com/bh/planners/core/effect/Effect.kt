package com.bh.planners.core.effect

import com.bh.planners.core.kether.game.ActionEffect
import com.bh.planners.core.pojo.Context
import taboolib.library.kether.QuestReader

abstract class Effect {

    abstract val name: String

    abstract fun sendTo(target: Target?, option: EffectOption, context: Context, response: ActionEffect.Response)

}
