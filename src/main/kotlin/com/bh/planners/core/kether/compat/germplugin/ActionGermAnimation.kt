package com.bh.planners.core.kether.compat.germplugin

import com.bh.planners.api.entity.ProxyEntity
import com.bh.planners.core.kether.containerOrSender
import com.germ.germplugin.api.GermPacketAPI
import com.germ.germplugin.api.bean.AnimDataDTO
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionGermAnimation {

    class Send(val state: ParsedAction<*>, val speed: ParsedAction<*>, val reverse: ParsedAction<*>, val selector: ParsedAction<*>?, ) : ScriptAction<Void>() {

        fun execute(entity: ProxyEntity, state: String, speed: Float, reverse: Boolean) {
            Bukkit.getOnlinePlayers().forEach {
                if (entity is Player) {
                    GermPacketAPI.sendBendAction(it, entity.entityId, AnimDataDTO(state, speed, reverse))
                } else {
                    GermPacketAPI.sendModelAnimation(it, entity.entityId, AnimDataDTO(state, speed, reverse))
                }
            }
        }

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            frame.run(state).str { id ->
                frame.run(speed).float { speed ->
                    frame.run(reverse).bool { reverse ->
                        frame.containerOrSender(selector).thenAccept {
                            it.forEachProxyEntity {
                                execute(this, id, speed, reverse)
                            }
                        }
                    }
                }
            }


            return CompletableFuture.completedFuture(null)
        }
    }

    class Stop(val state: ParsedAction<*>, val selector: ParsedAction<*>?, ) : ScriptAction<Void>() {

        fun execute(entity: ProxyEntity, state: String) {
            Bukkit.getOnlinePlayers().forEach {
                if (entity is Player) {
                    GermPacketAPI.sendBendClear(it, entity.entityId)
                } else {
                    GermPacketAPI.stopModelAnimation(it, entity.entityId, state)
                }
            }
        }

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            frame.run(state).str { id ->
                frame.containerOrSender(selector).thenAccept {
                    it.forEachProxyEntity {
                        execute(this, id)
                    }
                }
            }


            return CompletableFuture.completedFuture(null)
        }
    }
}