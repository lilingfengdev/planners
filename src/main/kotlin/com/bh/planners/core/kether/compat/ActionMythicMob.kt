package com.bh.planners.core.kether.compat

import com.bh.planners.core.kether.NAMESPACE
import com.bh.planners.core.kether.createTargets
import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import io.lumine.xikage.mythicmobs.MythicMobs
import io.lumine.xikage.mythicmobs.mobs.ActiveMob
import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionMythicMob {


    class MythicMobSpawn(
        val mob: ParsedAction<*>,
        val offsetX: ParsedAction<*>,
        val offsetY: ParsedAction<*>,
        val offsetZ: ParsedAction<*>,
        val selector: ParsedAction<*>,
    ) : ScriptAction<Target.Container>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Target.Container> {
            val future = CompletableFuture<Target.Container>()
            frame.newFrame(mob).run<Any>().thenAccept { mob ->
                frame.newFrame(offsetX).run<Any>().thenAccept { offsetX ->
                    frame.newFrame(offsetY).run<Any>().thenAccept { offsetY ->
                        frame.newFrame(offsetZ).run<Any>().thenAccept { offsetZ ->
                            frame.createTargets(selector).thenAccept { selector ->
                                val mobs = mutableListOf<ActiveMob>()
                                val mob = Coerce.toString(mob)
                                val offsetX = Coerce.toDouble(offsetX)
                                val offsetY = Coerce.toDouble(offsetY)
                                val offsetZ = Coerce.toDouble(offsetZ)
                                selector.forEachLocation {
                                    mobs += api.mobManager.spawnMob(mob, this.clone().add(offsetX, offsetY, offsetZ))
                                }
                                future.complete(Target.Container().also {
                                    it.addAll(mobs.map { it.livingEntity.toTarget() })
                                })
                            }
                        }
                    }
                }
            }
            return future
        }
    }


    companion object {

        val api by lazy { MythicMobs.inst() }

        @KetherParser(["mm"], namespace = NAMESPACE)
        fun parser() = scriptParser {
            it.switch {
                case("spawn") {
                    val mob = it.next(ArgTypes.ACTION)
                    val offsetX = it.next(ArgTypes.ACTION)
                    val offsetY = it.next(ArgTypes.ACTION)
                    val offsetZ = it.next(ArgTypes.ACTION)
                    val selector = it.next(ArgTypes.ACTION)
                    MythicMobSpawn(mob, offsetX, offsetY, offsetZ, selector)
                }
            }
        }

    }

}