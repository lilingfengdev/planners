package com.bh.planners.core.kether.game

import com.bh.planners.core.kether.NAMESPACE
import com.bh.planners.core.kether.game.entity.*
import com.bh.planners.core.kether.nextSelector
import com.bh.planners.core.kether.nextSelectorOrNull
import taboolib.module.kether.KetherParser
import taboolib.module.kether.expects
import taboolib.module.kether.scriptParser
import taboolib.module.kether.switch

class ActionEntity {


    companion object {

        /**
         * entity of [uuid: action]
         * entity loc [entity : action]
         * entity health [entity : action]
         * entity spawn type name health tick
         * entity set [yaw : action] [pitch : action] [selector]
         * entity remove [selector]
         */
        @KetherParser(["entity"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            it.switch {

                case("of") {
                    ActionEntityTransfer(it.nextParsedAction())
                }

                case("spawn") {
                    ActionEntitySpawn(
                        it.nextParsedAction(),
                        it.nextParsedAction(),
                        it.nextParsedAction(),
                        it.nextParsedAction(),
                        it.nextSelectorOrNull()
                    )
                }

                case("set") {
                    ActionEntitySet(
                        it.nextParsedAction(),
                        it.nextParsedAction(),
                        it.nextSelector()
                    )
                }

                case("remove") {
                    ActionEntityRemove(
                        it.nextSelector()
                    )
                }

                other {
                    try {
                        it.mark()
                        val expect = it.expects(*EntityField.fields().toTypedArray())
                        ActionEntityFieldGet(
                            EntityField.valueOf(expect.toUpperCase()),
                            it.nextSelectorOrNull() ?: error("the lack of 'they' cite target")
                        )
                    } catch (_: Throwable) {
                        it.reset()
                        error("error of case!")
                    }
                }
            }

        }

    }

}