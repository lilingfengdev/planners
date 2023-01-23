package com.bh.planners.core.kether.compat.germplugin

import com.bh.planners.core.kether.getLocation
import com.bh.planners.core.kether.readAccept
import com.germ.germplugin.api.GermSrcManager
import com.germ.germplugin.api.RootType
import com.germ.germplugin.api.dynamic.animation.GermAnimationMove
import com.germ.germplugin.api.dynamic.animation.GermAnimationPart
import org.bukkit.Location
import taboolib.common.platform.function.submit
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import java.util.*
import java.util.concurrent.CompletableFuture

class ActionGermEffectMove(val name: ParsedAction<*>, val pos1: ParsedAction<*>, val pos2: ParsedAction<*>) :
    ScriptAction<IEffectAnimation>() {

    override fun run(frame: ScriptFrame): CompletableFuture<IEffectAnimation> {
        val future = CompletableFuture<IEffectAnimation>()
        frame.readAccept<String>(name) { name ->

            frame.getLocation(pos1).thenAccept { pos1 ->
                frame.getLocation(pos2).thenAccept { pos2 ->
                    future.complete(EffectMove(name, pos1, pos2))
                }
            }
        }
        return future
    }

    class EffectMove(val name: String, val locA: Location, val locB: Location) : IEffectAnimation {

        val vectorAB = locB.clone().subtract(locA).toVector()
        val list = mutableListOf<Location>()
        val vectorLength = vectorAB.length()


        init {
            vectorAB.normalize()
        }

        override fun create(): GermAnimationMove {
            val animation = create(name) as GermAnimationMove
            vectorAB.multiply(vectorLength)
            animation.moveX = "${vectorAB.x}"
            animation.moveY = "${vectorAB.y}"
            animation.moveZ = "${vectorAB.z}"
            return animation
        }

    }

    companion object {

        private fun create(name: String): GermAnimationPart<*>? {
            val split = name.split(":")

            val configuration = ActionGermParticle.cache.computeIfAbsent(name) {
                GermSrcManager.getGermSrcManager().getSrc(split[0], RootType.ANIMATION)
            } ?: error("GermPlugin animation '$name' not found.")

            return GermAnimationPart.getGermAnimationPart(UUID.randomUUID().toString(), split[1], configuration)
        }


    }

}