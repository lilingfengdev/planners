package com.bh.planners.core.kether.game

import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.getLocation
import com.bh.planners.core.kether.*
import org.bukkit.Location
import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.KetherParser
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture

class ActionExplosion(
    val power: ParsedAction<*>,
    val selector: ParsedAction<*>?
) : ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        frame.newFrame(power).run<Any>().thenApply { power ->
            frame.containerOrOrigin(selector).thenAccept {
                it.forEach {
                    val loc = when (it) {
                        is Target.Entity -> it.proxy.location
                        is Target.Location -> it.value
                        else -> return@forEach
                    }
                    createExplosion(loc, Coerce.toFloat(power))
                }
            }

        }
        return CompletableFuture.completedFuture(null)
    }

    private fun createExplosion(target: Target, power: Float) {
        return createExplosion(target.getLocation() ?: return, power)
    }

    private fun createExplosion(loc: Location, power: Float) {
        loc.world!!.createExplosion(loc.x, loc.y, loc.z, power, false, false)
    }

    companion object {

        /**
         * 在指定(目标)坐标处召唤一次爆炸
         * explosion [power] [selector]
         */
        @KetherParser(["explosion"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            val power = it.nextParsedAction()
            ActionExplosion(power, it.nextSelectorOrNull())
        }
    }
}