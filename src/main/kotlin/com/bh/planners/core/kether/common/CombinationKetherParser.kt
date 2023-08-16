package com.bh.planners.core.kether.common

import taboolib.module.kether.ScriptActionParser

interface CombinationKetherParser {

    val id: Array<String>

    val namespace: String

    fun run(): ScriptActionParser<out Any?>

    annotation class Used
    
    annotation class Ignore
}