package com.bh.planners.core.kether.common


abstract class SimpleKetherParser(vararg id : String) : CombinationKetherParser {

    override val id = arrayOf(*id)

    override val namespace = "Planners"

    override fun toString(): String {
        return "SimpleKetherParser(id=${id.contentToString()}, namespace=${namespace})"
    }


}