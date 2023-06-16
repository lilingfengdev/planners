package com.bh.planners.core.kether.compat.germplugin

import com.bh.planners.core.kether.*
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
     * germ sound name type master volume 1.0 pitch 1.0 they "@self"
     *
     * 例子播放
     * germ effect [name: action] <selector>
     *
     * 特效移动动画
     * germ effect move [name: action] <selector>
     *
     * germ cooldown <slot> <tick> <selector>
     *
     */
    @KetherParser(["germengine", "germ", "germplugin"], namespace = NAMESPACE, shared = true)
    fun parser() = scriptParser {
        it.switch {
            case("animation") {
                when (it.expects("send", "stop")) {
                    "send" -> {
                        ActionGermAnimation(it.nextToken(), false, it.nextSelector())
                    }

                    "stop" -> {
                        ActionGermAnimation(it.nextToken(), true, it.nextSelector())
                    }

                    else -> error("out of case")
                }
            }
            case("cooldown") {
                ActionGermItemCooldown(it.nextParsedAction(), it.nextParsedAction(), it.nextSelectorOrNull())
            }
            case("sound") {
                ActionGermSound(
                    it.nextParsedAction(),
                    it.nextArgumentAction(arrayOf("soundtype", "type"), "MASTER")!!,
                    it.nextArgumentAction(arrayOf("volume"), 1)!!,
                    it.nextArgumentAction(arrayOf("pitch"), 1)!!,
                    it.nextSelector()
                )
            }
            case("look") {
                ActionGermLook(it.nextParsedAction(),it.nextArgumentAction(arrayOf("at"))!!,it.nextParsedAction())
            }
            case("effect") {

                try {
                    it.mark()
                    it.expects("move")
                    ActionGermEffectMove(
                        it.nextParsedAction(),
                        it.nextParsedAction(),
                        it.nextArgumentAction(arrayOf("to")) ?: error("lack 'to'")
                    )
                } catch (e: Exception) {
                    it.reset()
                    ActionGermParticle(
                        it.nextParsedAction(),
                        it.nextArgumentAction(arrayOf("animation"), "__none__")!!,
                        it.nextSelectorOrNull()
                    )
                }

            }

            case("stop") {
                ActionGermStop(it.nextParsedAction())
            }
        }
    }

}