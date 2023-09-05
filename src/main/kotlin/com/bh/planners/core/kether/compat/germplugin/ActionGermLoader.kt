package com.bh.planners.core.kether.compat.germplugin

import com.bh.planners.core.kether.*
import taboolib.module.kether.KetherParser
import taboolib.module.kether.expects
import taboolib.module.kether.scriptParser
import taboolib.module.kether.switch

object ActionGermLoader {


    /**
     * 模型/玩家动画播放
     * germ animation send [name: token] <speed: Float> <reverse: false> [they selector]
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
                        ActionGermAnimation.Send(
                            it.nextParsedAction(),
                            it.nextOptionalParsedAction(arrayOf("speed"), "1.0")!!,
                            it.nextOptionalParsedAction(arrayOf("reverse"), "false")!!,
                            it.nextSelectorOrNull()
                        )
                    }

                    "stop" -> {
                        ActionGermAnimation.Stop(it.nextParsedAction(), it.nextSelectorOrNull())
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
                    it.nextOptionalParsedAction(arrayOf("soundtype", "type"), "MASTER")!!,
                    it.nextOptionalParsedAction(arrayOf("volume"), 1)!!,
                    it.nextOptionalParsedAction(arrayOf("pitch"), 1)!!,
                    it.nextSelector()
                )
            }
            case("effect") {

                try {
                    it.mark()
                    when (it.expects("move", "projectile")) {
                        "move" -> ActionGermEffectMove(
                            it.nextParsedAction(),
                            it.nextParsedAction(),
                            it.nextOptionalParsedAction(arrayOf("to")) ?: error("lack 'to'")
                        )

                        "projectile" -> ActionGermEffectProjectile().also {
                            it.id = nextParsedAction()
                            it.duration = nextOptionalParsedAction("duration", "5000")!!
                            it.delay = nextOptionalParsedAction("delay", "-1")!!
                            it.transition = nextOptionalParsedAction("transition", "1000")!!
                            it.yaw = nextOptionalParsedAction(arrayOf("yaw"), ACTION_NULL)!!
                            it.pitch = nextOptionalParsedAction(arrayOf("pitch"), ACTION_NULL)!!
                            it.onhit = nextOptionalParsedAction(arrayOf("onhit"), ACTION_NULL)!!
                            it.collisionCount = nextOptionalParsedAction(arrayOf("count"), "1")!!
                            it.collisionRemove = nextOptionalParsedAction(arrayOf("remove"), "false")!!
                            it.selector = nextSelectorOrNull()
                            it.to = nextOptionalParsedAction("to")!!
                        }

                        else -> error("")
                    }
                } catch (e: Exception) {
                    it.reset()
                    ActionGermParticle(
                        it.nextParsedAction(),
                        it.nextOptionalParsedAction(arrayOf("animation"), "__none__")!!,
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
                        ActionGermLook(it.nextParsedAction(), it.nextOptionalParsedAction(arrayOf("at"))!!, it.nextParsedAction())
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