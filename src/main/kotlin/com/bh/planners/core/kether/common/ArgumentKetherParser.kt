package com.bh.planners.core.kether.common

import com.bh.planners.core.kether.common.KetherHelper.simpleKetherParser
import taboolib.library.kether.ParsedAction
import taboolib.library.kether.Parser
import taboolib.library.kether.QuestAction
import taboolib.library.kether.QuestActionParser
import taboolib.library.kether.QuestReader
import taboolib.module.kether.*


abstract class ArgumentScriptParser<T>(vararg id: String, val argument: ParsedAction<*>) : SimpleKetherParser(*id) {
    
    
    
}