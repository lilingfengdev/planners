package com.bh.planners.core.kether.compat.dragoncore

import com.bh.planners.core.kether.*
import taboolib.library.kether.ArgTypes
import taboolib.module.kether.*
import java.util.UUID

object ActionDragonLoader {

    /**
     * dragon animation send [name: token] [selector]
     * dragon animation stop [name: token] [selector]
     *
     * 音效播放
     * dragon sound name <volume: 1.0> <pitch: 1.0> <loop: false> [selector]
     * t: dragon sound xxx volume 1.0 pitch 1.0 loop true [selector]
     *
     * dragon effect [scheme: action] <rotation: action(0,0,0)> <time: action(100)> <they selector>
     *
     */
    @KetherParser(["dragon", "dragoncore"], namespace = NAMESPACE, shared = true)
    fun parser() = scriptParser {
        it.switch {
            case("animation") {
                when (it.expects("send", "stop")) {
                    "send" -> {
                        ActionDragonAnimation(it.nextToken(), false, it.nextParsedAction(), it.nextSelector())
                    }

                    "stop" -> {
                        ActionDragonAnimation(
                            it.nextToken(), true, it.nextParsedAction(), it.nextSelector()
                        )
                    }

                    else -> error("out of case")
                }
            }
            case("sound") {
                ActionDragonSound(
                    it.nextParsedAction(),
                    it.nextArgumentAction(arrayOf("volume"), 1.0f)!!,
                    it.nextArgumentAction(arrayOf("pitch"), 1.0f)!!,
                    it.nextArgumentAction(arrayOf("loop"), false)!!,
                    it.nextSelectorOrNull() ?: error("the lack of 'they' cite target")
                )
            }
            case("particle", "effect") {
                ActionDragonEffect(
                    it.nextParsedAction(),
                    it.nextArgumentAction(arrayOf("rotation"), "0,0,0")!!,
                    it.nextArgumentAction(arrayOf("time"), 100)!!,
                    it.nextSelectorOrNull()
                )
            }
        }
    }

}