package com.bh.planners.core.kether.compat.dragoncore

import com.bh.planners.core.kether.*
import taboolib.module.kether.*
import java.util.*

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
     * t: dragon bind entity &b bindEntity &a forward 0 offsetY 0 sideways 0 bindYaw true bindPitch true they "@server"
     *
     * 世界图片
     * dragon worldtexture send [key: String] [rotateX: Float] [rotateY: Float] [rotateZ: Float] [path: String] [width: Float] [height: Float] [alpha: Float] [followPlayer: Boolean] [glow: Boolean] [followEntity: Boolean] [x: Float] [y: Float] [z: Float] [player: selector]
     * dragon worldtexture stop [key: String] [selector]
     *
     * t: dragon worldtexture send "1" 0 0 0 "unknow.png" 10 5 1 true false true 0 0 0 "@server" they "@self"
     *
     * 运行方法
     * t: dragon runfunction default "方法.屏幕抖动(3,100,10,10);" they "@self"
     *
     * 运行实体控制方法
     * t: dragon entityfun "方法.设置动画变量('名字', 变量值);" "@fetch t" they "@self"
     *
     * 渲染一根绳索、未完成
     * dragon rope send [key: token] [path: token] [time: Tick] selector1 selector2
     * dragon rope stop [key: token]
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
                    it.nextOptionalAction(arrayOf("key"), UUID.randomUUID().toString())!!,
                    it.nextOptionalAction(arrayOf("type"), "VOICE")!!,
                    it.nextOptionalAction(arrayOf("volume"), 1.0f)!!,
                    it.nextOptionalAction(arrayOf("pitch"), 1.0f)!!,
                    it.nextOptionalAction(arrayOf("loop"), false)!!,
                    it.nextSelectorOrNull() ?: error("the lack of 'they' cite target")
                )
            }
            case("particle", "effect") {
                ActionDragonEffect(
                    it.nextParsedAction(),
                    it.nextOptionalAction(arrayOf("rotation"), "0,0,0")!!,
                    it.nextOptionalAction(arrayOf("time"), 100)!!,
                    it.nextSelectorOrNull()
                )
            }
            case("bind") {
                ActionDragonBind(
                    it.nextOptionalAction(arrayOf("entity")) ?: error("缺少entity"),
                    it.nextOptionalAction(arrayOf("bindEntity")) ?: error("缺少BindEntity"),
                    it.nextOptionalAction(arrayOf("forward"), "0")!!,
                    it.nextOptionalAction(arrayOf("offsetY"), "0")!!,
                    it.nextOptionalAction(arrayOf("sideways"), "0")!!,
                    it.nextOptionalAction(arrayOf("bindYaw"), "true")!!,
                    it.nextOptionalAction(arrayOf("bindPitch"), "true")!!,
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
                ActionDragonRunFunction(it.nextToken(), it.nextToken(), it.nextSelectorOrNull())
            }
            case("entityfun") {
                ActionDragonEntityRunFunction(it.nextToken(), it.nextParsedAction(), it.nextSelectorOrNull())
            }
            case("rope") {
                when (it.expects("send", "stop")) {
                    "send" -> {
                        ActionDragonRopeSend(it.nextToken(), it.nextToken(), it.nextParsedAction(), it.nextParsedAction(), it.nextSelectorOrNull())
                    }
                    "stop" -> ActionDragonRopeStop(it.nextToken())
                    else -> error("out of case")
                }
            }
        }
    }

}