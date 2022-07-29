package com.bh.planners.core.kether.compat.dragoncore

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
        frame.runTransfer<String>(scheme) { scheme ->
            frame.runTransfer<String>(rotation) { rotation ->
                frame.runTransfer<Int>(time) { time ->
                    val id = UUID.randomUUID().toString()
                    if (selector != null) {
                        frame.execEntity(selector) { execute(id, scheme, rotation, time, this) }
                    } else {
                        execute(id, scheme, rotation, time, frame.asPlayer() ?: return@runTransfer)
                    }
                }
            }
        }

        return CompletableFuture.completedFuture(null)
    }

    fun execute(id: String, scheme: String, rotation: String, time: Int, target: Entity) {
        val entityId = target.uniqueId.toString()
        Bukkit.getOnlinePlayers().forEach {
            PacketSender.addParticle(it, scheme, id, entityId, rotation, time)
        }
    }

}