package com.bh.planners.core.kether.game

import com.bh.planners.core.kether.*
import com.bh.planners.core.kether.game.entity.*
import taboolib.library.kether.ArgTypes
import taboolib.module.kether.*

class ActionEntity {



    companion object {

        /**
         * entity of [uuid: action]
         * entity loc [entity : action]
         * entity health [entity : action]
         * entity spawn type name health tick
         */
        @KetherParser(["entity"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            it.switch {

                case("of") {
                    ActionEntityTransfer(it.nextParsedAction())
                }

                case("spawn") {
                    ActionEntitySpawn(it.nextParsedAction(), it.nextParsedAction(), it.nextParsedAction(), it.nextParsedAction(), it.selectorAction())
                }

                other {
                    try {
                        it.mark()
                        val expect = it.expects(*EntityField.fields().toTypedArray())
                        ActionEntityFieldGet(EntityField.valueOf(expect.uppercase()),it.selectorAction() ?: error("the lack of 'they' cite target"))
                    }catch (_: Throwable) {
                        it.reset()
                        error("error of case!")
                    }
                }
            }

        }

    }

}