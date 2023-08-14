package com.bh.planners.core.kether.compat.dragoncore

import com.bh.planners.core.kether.createContainer
import com.bh.planners.core.kether.readAccept
import eos.moe.dragoncore.network.PacketSender
import org.bukkit.entity.Player
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import java.util.*
import java.util.concurrent.CompletableFuture

class ActionDragonBind(
    val entity: ParsedAction<*>,
    val bindEntity: ParsedAction<*>,
    val forward: ParsedAction<*>,
    val offsetY: ParsedAction<*>,
    val sideways: ParsedAction<*>,
    val bindYaw: ParsedAction<*>,
    val bindPitch: ParsedAction<*>,
    val selector: ParsedAction<*>,
) : ScriptAction<Void>() {

    fun execute(player: Player, entity: UUID, bindEntity: UUID, bindYaw: Boolean, bindPitch: Boolean, forward: Float, offsetY: Float, sideways: Float) {
        PacketSender.sendEntityLocationBind(
            player,
            entity,
            bindEntity,
            forward,
            offsetY,
            sideways,
            bindYaw,
            bindPitch
        )
    }

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {

            frame.readAccept<String>(entity) { entity ->
                frame.readAccept<String>(bindEntity) { bindEntity ->
                    frame.readAccept<Boolean>(bindYaw) { bindYaw ->
                        frame.readAccept<Boolean>(bindPitch) { bindPitch ->
                            frame.readAccept<Float>(forward) { forward ->
                                frame.readAccept<Float>(offsetY) { offsetY ->
                                    frame.readAccept<Float>(sideways) { sideways ->
                                        frame.createContainer(selector).thenAccept {
                                            it.forEachPlayer {
                                                execute(
                                                    this,
                                                    UUID.fromString(entity),
                                                    UUID.fromString(bindEntity),
                                                    bindYaw,
                                                    bindPitch,
                                                    forward,
                                                    offsetY,
                                                    sideways
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        return CompletableFuture.completedFuture(null)

    }
}