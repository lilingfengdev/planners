package com.bh.planners.core.kether.common

import taboolib.library.kether.QuestContext
import taboolib.library.kether.QuestReader
import taboolib.module.kether.ScriptActionParser
import taboolib.module.kether.combinationParser

abstract class SimpleKetherParser(vararg name : String) : CombinationKetherParser {

    override val id = arrayOf(*name)
    
    override val namespace = arrayOf("Planners","planners","planners-skill")
    
    
}