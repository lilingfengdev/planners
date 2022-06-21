package com.bh.planners.core.kether.event

import taboolib.library.kether.QuestAction
import taboolib.library.kether.QuestActionParser
import taboolib.library.kether.QuestReader
import taboolib.module.kether.ScriptAction

class ActionEventParser(val reader: QuestReader.() -> ScriptAction<*>) : QuestActionParser {

    @Suppress("UNCHECKED_CAST")
    override fun <T> resolve(resolver: QuestReader): QuestAction<T> {
        return reader(resolver) as ScriptAction<T>
    }
}