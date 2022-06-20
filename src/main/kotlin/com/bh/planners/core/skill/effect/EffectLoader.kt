package com.bh.planners.core.skill.effect

import taboolib.common.reflect.Reflex.Companion.invokeConstructor
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.QuestReader

abstract class EffectLoader<T : Effect> {

    abstract val name: String

    abstract val clazz: Class<T>

    open fun parser(render: QuestReader): T {
        val optionAction = render.next(ArgTypes.ACTION)
        return clazz.invokeConstructor(optionAction)
    }

}
