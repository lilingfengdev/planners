package com.bh.planners.core.kether.compat.virtualentityskillmodule

import com.bh.planners.core.kether.NAMESPACE
import com.bh.planners.core.kether.nextSelectorOrNull
import taboolib.module.kether.KetherParser
import taboolib.module.kether.scriptParser
import taboolib.module.kether.switch

object ActionModuleLoader {

    @KetherParser(["module"], namespace = NAMESPACE, shared = true)
    fun parser() = scriptParser {
        it.switch {
            case("bind") {
                ActionModuleBind(
                    it.nextParsedAction(),
                    it.nextParsedAction(),
                    it.nextParsedAction(),
                    it.nextSelectorOrNull()
                )
            }
            case("clear") {
                ActionModuleClear(
                    it.nextParsedAction(),
                    it.nextSelectorOrNull()
                )
            }
        }
    }

}