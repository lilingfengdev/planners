package com.bh.planners.core.kether.common

import taboolib.library.kether.QuestActionParser
import taboolib.module.kether.ScriptActionParser

interface CombinationKetherParser {

    val id: Array<String>

    val namespace: String

    fun run(): QuestActionParser

    annotation class Used
    
    annotation class Ignore
}