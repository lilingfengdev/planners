package com.bh.planners.core.kether.game.event

import com.bh.planners.core.feature.presskey.Emitter
import com.bh.planners.core.kether.NAMESPACE
import com.bh.planners.core.kether.get
import com.bh.planners.core.kether.nextSelectorOrNull
import com.bh.planners.core.kether.nextArgumentAction
import taboolib.library.kether.ArgTypes
import taboolib.module.kether.KetherParser
import taboolib.module.kether.scriptParser
import taboolib.module.kether.switch

class ActionEventSubscribers {


    companion object {

        @KetherParser(["on"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            it.switch {
                case("keypress") {
                    ActionKeyPress(
                        it.nextParsedAction(),
                        it.nextArgumentAction(arrayOf("timeout"), Emitter.timeout / 50)!!,
                        it.nextSelectorOrNull(),
                        it.get(arrayOf("then"))
                    )
                }
            }
        }

    }

}