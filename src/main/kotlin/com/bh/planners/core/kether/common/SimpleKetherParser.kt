package com.bh.planners.core.kether.common

import taboolib.module.kether.ScriptActionParser


abstract class SimpleKetherParser(vararg id : String) : CombinationKetherParser {

    override val id = arrayOf(*id)

    override val namespace = "kether"

    override fun toString(): String {
        return "SimpleKetherParser(id=${id.contentToString()}, namespace=${namespace})"
    }

}