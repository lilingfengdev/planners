package com.bh.planners.core.kether.compat.dragoncore

import com.bh.planners.core.kether.*
import eos.moe.dragoncore.api.CoreAPI
import eos.moe.dragoncore.network.PacketSender
import org.bukkit.entity.LivingEntity
import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionDragonCore {


    class ActionAnimation(
        val state: String,
        val remove: Boolean,
        val transition: ParsedAction<*>,
        val selector: ParsedAction<*>
    ) :
        ScriptAction<Void>() {

        fun execute(entity: LivingEntity, state: String, remove: Boolean, transitionTime: Int) {
            if (remove) {
                CoreAPI.removeEntityAnimation(entity, state, transitionTime)
            } else {
                CoreAPI.setEntityAnimation(entity, state, transitionTime)
            }


        }

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {

            frame.newFrame(transition).run<Any>().thenAccept {
                val transition = Coerce.toInteger(it)
                frame.execLivingEntity(selector) { execute(this, state, remove, transition) }
            }
            return CompletableFuture.completedFuture(null)
        }
    }

    class ActionSound(
        val name: ParsedAction<*>,
        val volume: ParsedAction<*>,
        val pitch: ParsedAction<*>,
        val loop: ParsedAction<*>,
        val selector: ParsedAction<*>
    ) :
        ScriptAction<Void>() {


        override fun run(frame: ScriptFrame): CompletableFuture<Void> {

            frame.runTransfer<String>(name) { name ->
                frame.runTransfer<Float>(volume) { volume ->
                    frame.runTransfer<Float>(pitch) { pitch ->
                        frame.runTransfer<Boolean>(loop) { loop ->
                            frame.execPlayer(selector) {
                                PacketSender.sendPlaySound(player, name, volume, pitch, loop, 0f, 0f, 0f)
                            }
                        }

                    }
                }
            }

            return CompletableFuture.completedFuture(null)
        }
    }

    companion object {

        /**
         * dragon animation send [name: token] [selector]
         * dragon animation stop [name: token] [selector]
         *
         * 音效播放
         * dragon sound name <volume: 1.0> <pitch: 1.0> <loop: false> [selector]
         * t: dragon sound xxx volume 1.0 pitch 1.0 loop true [selector]
         *
         */
        @KetherParser(["dragon", "dragoncore"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            it.switch {
                case("animation") {
                    when (it.expects("send", "stop")) {
                        "send" -> {
                            ActionAnimation(it.nextToken(), false, it.next(ArgTypes.ACTION), it.selector())
                        }

                        "stop" -> {
                            ActionAnimation(
                                it.nextToken(),
                                true,
                                it.next(ArgTypes.ACTION),
                                it.selector()
                            )
                        }


                        else -> error("out of case")
                    }
                }
                case("sound") {
                    ActionSound(
                        it.next(ArgTypes.ACTION),
                        it.tryGet(arrayOf("volume"), 1.0f)!!,
                        it.tryGet(arrayOf("pitch"), 1.0f)!!,
                        it.tryGet(arrayOf("loop"), false)!!,
                        it.selectorAction() ?: error("the lack of 'they' cite target")
                    )
                }
            }
        }
    }

}