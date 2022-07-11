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
         * entity spawn type name health tick 返回 [ UUID ]
         */
        @KetherParser(["entity"], namespace = NAMESPACE)
        fun parser() = scriptParser {
            it.switch {

                case("of") {
                    ActionOfEntity(it.next(ArgTypes.ACTION))
                }

                case("spawn") {
                    ActionEntitySpawn(it.next(ArgTypes.ACTION), it.next(ArgTypes.ACTION), it.next(ArgTypes.ACTION), it.next(ArgTypes.ACTION), it.selectorAction())
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