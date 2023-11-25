package com.bh.planners.core.kether.compat.dragoncore

import com.bh.planners.core.effect.Target
import com.bh.planners.core.kether.exec
import com.bh.planners.core.kether.local
import com.bh.planners.core.kether.origin
import com.bh.planners.core.kether.readAccept
import eos.moe.dragoncore.network.PacketSender
import org.bukkit.Bukkit
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import java.util.*
import java.util.concurrent.CompletableFuture

class ActionDragonEffect(
    val scheme: ParsedAction<*>,
    val rotation: ParsedAction<*>,
    val time: ParsedAction<*>,
    val selector: ParsedAction<*>?,
) : ScriptAction<Void>() {


    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        frame.readAccept<String>(scheme) { scheme ->
            frame.readAccept<String>(rotation) { rotation ->
                frame.readAccept<Int>(time) { time ->
                    val id = UUID.randomUUID().toString()
                    if (selector != null) {
                        frame.exec(selector) {
                            execute(id, scheme, rotation, time, this)
                        }
                    } else {
                        execute(id, scheme, rotation, time, frame.origin())
                    }
                }
            }
        }

        return CompletableFuture.completedFuture(null)
    }

    fun execute(id: String, scheme: String, rotation: String, time: Int, target: Target) {

        val value = when (target) {
            is Target.Entity -> {
                target.proxy.uniqueId.toString()
            }

            is Target.Location -> {
                target.value.local()
            }

            else -> return
        }

        Bukkit.getOnlinePlayers().forEach {
            PacketSender.addParticle(it, scheme, id, value, rotation, time)
        }
    }

}