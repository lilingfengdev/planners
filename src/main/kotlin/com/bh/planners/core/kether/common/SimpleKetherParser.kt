package com.bh.planners.core.kether.common

import com.bh.planners.core.kether.NAMESPACE


abstract class SimpleKetherParser(vararg id : String) : CombinationKetherParser {

    override val id = arrayOf(*id)

    override val namespace = NAMESPACE

    override fun toString(): String {
        return "SimpleKetherParser(id=${id.contentToString()}, namespace=${namespace})"
    }

}