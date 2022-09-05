package com.bh.planners.core.kether.compat.adyeshach

import com.bh.planners.core.kether.*
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*

object ActionAdyeshach {


    /**
     * adyeshach spawn type name tick
     * adyeshach follow <option: action> [owner:first] [selector:entity]
     *
     * adyeshach script file args[] selector
     *
     * adyeshach remove [selector]
     *
     */
    @KetherParser(["adyeshach", "ady"], namespace = NAMESPACE, shared = true)
    fun parser() = scriptParser {
        it.switch {
            case("spawn") {
                ActionAdyeshachSpawn(it.nextParsedAction(), it.nextParsedAction(), it.nextParsedAction(), it.selectorAction())
            }
            case("follow") {
                ActionAdyeshachFollow(it.nextParsedAction(), it.nextParsedAction(), it.tryGet(arrayOf("option","params"),"EMPTY")!!)
            }
            case("script") {
                ActionAdyeshachScript(it.nextParsedAction(), it.next(ArgTypes.listOf(ArgTypes.ACTION)),it.selectorAction())
            }
            case("remove") {
                ActionAdyeshachRemove(it.selector())
            }
        }

    }

    fun ScriptFrame.execAdyeshachEntity(selector: ParsedAction<*>, call: AdyeshachEntity.() -> Unit) {
        execEntity(selector) {
            if (this is AdyeshachEntity) {
                call(this)
            }
        }
    }


}