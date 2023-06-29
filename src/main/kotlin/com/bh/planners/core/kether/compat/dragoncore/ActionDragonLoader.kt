package com.bh.planners.core.kether.compat.dragoncore

import com.bh.planners.core.kether.*
import eos.moe.dragoncore.api.CoreAPI
import eos.moe.dragoncore.network.PacketSender
import taboolib.module.kether.*
import java.util.UUID

object ActionDragonLoader {

    /**
     * 实体动画
     * dragon animation send [name: token] [transition: action] [selector]
     * dragon animation stop [name: token] [transition: action] [selector]
     *
     * 玩家动画
     * dragon playeranimation send [name: token] [selector]
     * dragon playeranimation stop [selector]
     *
     * 音效播放
     * dragon sound name <key: random.uuid> <type: music> <volume: 1.0> <pitch: 1.0> <loop: false> [selector]
     * t: dragon sound xxx key 王八 type music volume 1.0 pitch 1.0 loop true [selector]
     *
     * dragon effect [scheme: action] <rotation: action(0,0,0)> <time: action(100)> <they selector>
     *
     * 实体绑定
     * dragon bind <entity: UUID> <bindEntity: UUID> <forward: Float> <offsetY: Float> <sideways: Float> <bindYaw: true> <bindPitch: true> <they selector>
     *
     * t: selector jw to ady spawn ARMOR_STAND 剑舞 20 they "@offset 0 -1.5 0"
     * t: set a to entity uuid they "@self"
     * t: set b to entity uuid they "@fetch jw"
     * t: dragon bind entity &b bindEntity &a forward 0 offsetY 0 sideways 0 bindYaw true bindPitch true they "@self"
     *
     * 世界图片
     * dragon worldtexture send [key: String] [rotateX: Float] [rotateY: Float] [rotateZ: Float] [path: String] [width: Float] [height: Float] [alpha: Float] [followPlayer: Boolean] [glow: Boolean] [followEntity: Boolean] [x: Float] [y: Float] [z: Float] [player: selector] [selector]
     * dragon worldtexture stop [key: String] [selector]
     *
     * t: dragon worldtexture send "1" 0 0 0 "unknow.png" 10 5 1 true false true 0 0 0 "@server" they "@self"
     *
     * 运行方法
     * t: dragon runfunction default "方法.屏幕抖动(3,100,10,10);" they "@self"
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
            case("playeranimation"){
                when (it.expects("send", "stop", "clear")) {
                    "send" -> {
                        ActionDragonPlayerAnimation(it.nextToken(),false,it.nextSelector())
                    }

                    "stop" -> {
                        ActionDragonPlayerAnimation("null",true,it.nextSelector())
                    }

                    else -> error("out of case")
                }
            }
            case("sound") {
                ActionDragonSound(
                    it.nextParsedAction(),
                    it.nextArgumentAction(arrayOf("key"), UUID.randomUUID().toString())!!,
                    it.nextArgumentAction(arrayOf("type"), "music")!!,
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
            case("bind") {
                ActionDragonBind(
                    it.nextArgumentAction(arrayOf("entity")) ?: error("缺少entity"),
                    it.nextArgumentAction(arrayOf("bindEntity")) ?: error("缺少BindEntity"),
                    it.nextArgumentAction(arrayOf("forward"), "0")!!,
                    it.nextArgumentAction(arrayOf("offsetY"), "0")!!,
                    it.nextArgumentAction(arrayOf("sideways"), "0")!!,
                    it.nextArgumentAction(arrayOf("bindYaw"), "true")!!,
                    it.nextArgumentAction(arrayOf("bindPitch"), "true")!!,
                    it.nextSelectorOrNull() ?: error("the lack of 'they' cite target")
                )
            }
            case("worldtexture") {
                when (it.expects("send", "stop")) {
                    "send" -> {
                        ActionDragonWorldTextureSet(
                            it.nextParsedAction(),
                            it.nextParsedAction(),
                            it.nextParsedAction(),
                            it.nextParsedAction(),
                            it.nextParsedAction(),
                            it.nextParsedAction(),
                            it.nextParsedAction(),
                            it.nextParsedAction(),
                            it.nextParsedAction(),
                            it.nextParsedAction(),
                            it.nextParsedAction(),
                            it.nextParsedAction(),
                            it.nextParsedAction(),
                            it.nextParsedAction(),
                            it.nextParsedAction(),
                            it.nextSelectorOrNull() ?: error("the lack of 'they' cite target")
                        )
                    }
                    "stop" -> {
                        ActionDragonWorldTextureRemove(
                            it.nextParsedAction(),
                            it.nextSelectorOrNull() ?: error("the lack of 'they' cite target")
                        )
                    }
                    else -> error("out of case")
                }
            }
            case("runfunction") {
                ActionDragonRunFunction(it.nextParsedAction(), it.nextToken(), it.nextSelectorOrNull())
            }
        }
    }

}