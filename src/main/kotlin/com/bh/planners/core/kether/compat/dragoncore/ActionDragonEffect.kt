package com.bh.planners.core.kether.compat.dragoncore

import com.bh.planners.core.effect.Target
import com.bh.planners.core.kether.*
import eos.moe.dragoncore.network.PacketSender
import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.UUID
import java.util.concurrent.CompletableFuture

class ActionDragonEffect(
    val scheme: ParsedAction<*>,
    val rotation: ParsedAction<*>,
    val time: ParsedAction<*>,
    val selector: ParsedAction<*>?
) : ScriptAction<Void>() {


    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        frame.runTransfer0<String>(scheme) { scheme ->
            frame.runTransfer0<String>(rotation) { rotation ->
                frame.runTransfer0<Int>(time) { time ->
                    val id = UUID.randomUUID().toString()
                    if (selector != null) {
                        frame.exec(selector) {
                            execute(id, scheme, rotation, time, this)
                        }
                    } else {
                        execute(id, scheme, rotation, time, frame.origin() ?: return@runTransfer0)
                    }
                }
            }
        }

        return CompletableFuture.completedFuture(null)
    }

    fun execute(id: String, scheme: String, rotation: String, time: Int, target: Target) {

        val value = when (target) {
            is Target.Entity -> {
                target.entity.uniqueId.toString()
            }

            is Target.Location -> {
                target.value.toLocal()
            }

            else -> return
        }

        Bukkit.getOnlinePlayers().forEach {
            PacketSender.addParticle(it, scheme, id, value, rotation, time)
        }
    }

}