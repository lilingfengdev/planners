package com.bh.planners.core.kether.compat.dragoncore

import com.bh.planners.core.kether.createContainer
import com.bh.planners.core.kether.execEntity
import com.bh.planners.core.kether.readAccept
import eos.moe.dragoncore.api.CoreAPI
import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import java.util.concurrent.CompletableFuture

class ActionDragonWorldTextureSet(
    val key: ParsedAction<*>,
    val rotateX: ParsedAction<*>,
    val rotateY: ParsedAction<*>,
    val rotateZ: ParsedAction<*>,
    val path: ParsedAction<*>,
    val width: ParsedAction<*>,
    val height: ParsedAction<*>,
    val alpha: ParsedAction<*>,
    val followPlayer: ParsedAction<*>,
    val glow: ParsedAction<*>,
    val followEntity: ParsedAction<*>,
    val x: ParsedAction<*>,
    val y: ParsedAction<*>,
    val z: ParsedAction<*>,
    val player: ParsedAction<*>,
    val selector: ParsedAction<*>,
) : ScriptAction<Void>() {

    fun execute(
        key: String,
        rotateX: Float,
        rotateY: Float,
        rotateZ: Float,
        path: String,
        width: Float,
        height: Float,
        alpha: Float,
        followPlayer: Boolean,
        glow: Boolean,
        followEntity: Boolean,
        x: Float,
        y: Float,
        z: Float,
        player: Player,
        entity: Entity
    ) {
        val loc = entity.location
            CoreAPI.setPlayerWorldTexture(
                player,
                key,
                loc,
                rotateX,
                rotateY,
                rotateZ,
                path,
                width,
                height,
                alpha,
                followPlayer,
                glow,
                entity.uniqueId,
                followEntity,
                x,
                y,
                z
            )
    }

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {

        frame.readAccept<String>(key) { key ->
            frame.readAccept<Float>(rotateX) { rotateX ->
                frame.readAccept<Float>(rotateY) { rotateY ->
                    frame.readAccept<Float>(rotateZ) { rotateZ ->
                        frame.readAccept<String>(path) { path ->
                            frame.readAccept<Float>(width) { width ->
                                frame.readAccept<Float>(height) { height ->
                                    frame.readAccept<Float>(alpha) { alpha ->
                                        frame.readAccept<Boolean>(followPlayer) { followPlayer ->
                                            frame.readAccept<Boolean>(glow) { glow ->
                                                frame.readAccept<Boolean>(followEntity) { followEntity ->
                                                    frame.readAccept<Float>(x) { x ->
                                                        frame.readAccept<Float>(y) { y ->
                                                            frame.readAccept<Float>(z) { z ->
                                                                frame.createContainer(selector)
                                                                    .thenAccept { container ->
                                                                        frame.execEntity(selector) {
                                                                            val entity = this
                                                                            container.forEachPlayer {
                                                                                execute(
                                                                                    key,
                                                                                    rotateX,
                                                                                    rotateY,
                                                                                    rotateZ,
                                                                                    path,
                                                                                    width,
                                                                                    height,
                                                                                    alpha,
                                                                                    followPlayer,
                                                                                    glow,
                                                                                    followEntity,
                                                                                    x,
                                                                                    y,
                                                                                    z,
                                                                                    this,
                                                                                    entity
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