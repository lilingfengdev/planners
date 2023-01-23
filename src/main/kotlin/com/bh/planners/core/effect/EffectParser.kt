package com.bh.planners.core.effect

import taboolib.library.kether.QuestReader
import taboolib.module.kether.ScriptAction

interface EffectParser {

    fun parser(reader: QuestReader): ScriptAction<*>

}