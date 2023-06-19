package com.bh.planners.core.kether.compat.germplugin

import com.bh.planners.core.kether.*
import taboolib.module.kether.KetherParser
import taboolib.module.kether.expects
import taboolib.module.kether.scriptParser
import taboolib.module.kether.switch

object ActionGermLoader {


    /**
     * 模型/玩家动画播放
     * germ animation send [name: token] [they selector]
     * germ animation stop [name: token] [they selector]
     *
     * 音效播放
     * germ sound name <type: action(master)> <volume: action(1)> <pitch: action(1)> [they selector]
     * germ sound name type master volume 1.0 pitch 1.0 they "@self"
     *
     * 例子播放
     * germ effect [name: action] [they selector]
     *
     * 特效移动动画
     * germ effect move [name: action] [they selector]
     *
     * 物品冷却
     * germ cooldown <slot> <tick> [they selector]
     *
     * 锁定移动（客户端行为，不会产生抽搐）
     * germ move lock [duration/-1] [they selector]
     * germ move unlock [they selector]
     *
     * 锁定视角（客户端行为，不会产生抽搐）
     * germ look at [duration/-1] <selector> [they selector]
     * germ look lock [duration/-1] [they selector]
     * germ look unlock [they selector]
     *
     * 锁定视角类型
     * germ view lock [duration/-1] [type: 1,2,3] [they selector]
     * germ view unlock [they selector]
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

            case("move") {
                when (it.expects("lock", "unlock")) {
                    "lock" -> {
                        ActionGermLock.LockPlayerMove(it.nextParsedAction(), it.nextSelector())
                    }
                    "unlock" -> {
                        ActionGermLock.UnLockPlayerMove(it.nextSelector())
                    }

                    else -> error("out of case")
                }
            }

            case("look") {
                when (it.expects("lock", "unlock")) {
                    "lock" -> {
                        ActionGermLock.LockPlayerView(it.nextParsedAction(), it.nextSelector())
                    }
                    "unlock" -> {
                        ActionGermLock.UnLockPlayerView(it.nextSelector())
                    }
                    "at" -> {
                        ActionGermLook(it.nextParsedAction(),it.nextArgumentAction(arrayOf("at"))!!,it.nextParsedAction())
                    }
                    else -> error("out of case")
                }
            }
            case("view") {
                when (it.expects("lock", "unlock")) {
                    "lock" -> {
                        ActionGermLock.LockPlayerViewType(it.nextParsedAction(), it.nextParsedAction(), it.nextSelector())
                    }
                    "unlock" -> {
                        ActionGermLock.UnLockPlayerViewType(it.nextSelector())
                    }
                    else -> error("out of case")
                }
            }

            case("stop") {
                ActionGermStop(it.nextParsedAction())
            }
        }
    }

}