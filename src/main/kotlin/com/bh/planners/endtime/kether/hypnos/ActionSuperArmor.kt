package com.bh.planners.endtime.kether.hypnos

import com.bh.planners.core.kether.NAMESPACE
import com.bh.planners.core.kether.containerOrSender
import com.bh.planners.core.kether.nextSelectorOrNull
import com.github.hypnos.api.HypnosAPI.hypnosProfile
import org.bukkit.entity.Player
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionSuperArmor(
    val time: ParsedAction<*>,
    val selector: ParsedAction<*>?,
) : ScriptAction<Void>() {

    fun execute(player: Player, duration: Double) {
        player.hypnosProfile.superArmor = System.currentTimeMillis() + (duration * 50).toLong()
    }

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        frame.run(time).double { time ->
            frame.containerOrSender(selector).thenAccept {
                it.forEachPlayer {
                    execute(this, time)
                }
            }
        }
        return CompletableFuture.completedFuture(null)
    }


    companion object {
        /**
         * 金身
         * hypnos super [time: double] [they selector]
         */
        @KetherParser(["hy", "hypnos"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            it.switch {
                case("super") {
                    ActionSuperArmor(it.nextParsedAction(), it.nextSelectorOrNull())
                }
            }
        }
    }


}