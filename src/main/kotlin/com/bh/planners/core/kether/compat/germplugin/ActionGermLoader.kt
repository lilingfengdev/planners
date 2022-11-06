package com.bh.planners.core.kether.compat.germplugin

import com.bh.planners.core.kether.NAMESPACE
import com.bh.planners.core.kether.selector
import com.bh.planners.core.kether.selectorAction
import com.bh.planners.core.kether.tryGet
import taboolib.library.kether.ArgTypes
import taboolib.module.kether.KetherParser
import taboolib.module.kether.expects
import taboolib.module.kether.scriptParser
import taboolib.module.kether.switch

object ActionGermLoader {


    /**
     * germ animation send [name: token] [selector]
     *
     * germ animation stop [name: token] [selector]
     *
     * 音效播放
     * germ sound name <type: action(master)> <volume: action(1)> <pitch: action(1)> <selector>
     *
     * germ sound name type master volume 1.0 pitch 1.0 they "-@self"
     *
     * 例子播放
     * germ effect [name: action] <selector>
     */
    @KetherParser(["germengine", "germ", "germplugin"], namespace = NAMESPACE, shared = true)
    fun parser() = scriptParser {
        it.switch {
            case("animation") {
                when (it.expects("send", "stop")) {
                    "send" -> {
                        ActionGermAnimation(it.nextToken(), false, it.selector())
                    }

                    "stop" -> {
                        ActionGermAnimation(it.nextToken(), true, it.selector())
                    }

                    else -> error("out of case")
                }
            }
            case("sound") {
                ActionGermSound(
                    it.nextParsedAction(),
                    it.tryGet(arrayOf("soundtype", "type"), "MASTER")!!,
                    it.tryGet(arrayOf("volume"), 1)!!,
                    it.tryGet(arrayOf("pitch"), 1)!!,
                    it.selector()
                )
            }
//            case("look") {
//                ActionGermLook(it.nextParsedAction(),it.tryGet(arrayOf("at"))!!)
//            }
            case("effect") {
                ActionGermParticle(it.nextParsedAction(), it.selectorAction())
            }
        }
    }

}