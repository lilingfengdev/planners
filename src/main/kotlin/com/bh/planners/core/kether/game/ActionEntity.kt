package com.bh.planners.core.kether.game

import com.bh.planners.core.kether.NAMESPACE
import com.bh.planners.core.kether.game.entity.*
import com.bh.planners.core.kether.nextOptionalAction
import com.bh.planners.core.kether.nextSelector
import com.bh.planners.core.kether.nextSelectorOrNull
import taboolib.module.kether.KetherParser
import taboolib.module.kether.expects
import taboolib.module.kether.scriptParser
import taboolib.module.kether.switch
import java.util.*

object ActionEntity {

    /**
     * entity of [uuid: action]
     * entity loc [entity : action]
     * entity health [entity : action]
     * entity spawn type name tick <health: health> <vector: bool;def: false>
     * entity set view [yaw : action] [pitch : action] [selector]
     * entity set viewto [selector] [selector]
     * entity set arrowsInBody add/set/dec [arrows : Int] [selector]
     * entity remove [selector]
     * entity gravity [gravity: bool] [selector]
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
                    it.nextOptionalAction(arrayOf("health","h"), "0")!!,
                    it.nextOptionalAction(arrayOf("vector","v"), "false")!!,
                    it.nextSelectorOrNull()
                )
            }

            case("set") {
                when (it.expects("view", "arrowsInBody", "viewto")) {
                    "view" -> {
                        ActionEntitySet(
                            it.nextParsedAction(),
                            it.nextParsedAction(),
                            it.nextSelector()
                        )
                    }

                    "viewto" -> {
                        ActionEntitySetto(
                            it.nextParsedAction(),
                            it.nextSelectorOrNull()
                        )
                    }

                    "arrowsInBody" -> {
                        ActionEntitySetArrows(
                            it.nextParsedAction(),
                            it.nextParsedAction(),
                            it.nextSelector()
                        )
                    }

                    else -> error("out of case")
                }
            }

            case("remove") {
                ActionEntityRemove(
                    it.nextSelector()
                )
            }

            case("gravity") {
                ActionEntityGravity(
                    it.nextParsedAction(),
                    it.nextSelector()
                )
            }

            other {
                try {
                    it.mark()
                    val expect = it.expects(*EntityField.fields().toTypedArray())
                    ActionEntityFieldGet(
                        EntityField.valueOf(expect.uppercase(Locale.getDefault())),
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