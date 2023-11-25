package com.bh.planners.core.kether.game

import com.bh.planners.core.effect.Target
import com.bh.planners.core.kether.*
import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import org.bukkit.Bukkit
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.lang.reflect.InvocationTargetException
import java.util.concurrent.CompletableFuture


class ActionLeashHolder {

    class ActionLeashSet(
        val selector: ParsedAction<*>,
        val holder: ParsedAction<*>?,
    ) : ScriptAction<Void>() {

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            frame.createContainer(selector).thenAccept { container ->
                frame.containerOrSender(holder).thenAccept last@{
                    val protocolManager = ProtocolLibrary.getProtocolManager()

                    val holder = container.firstEntityTarget()?.entityId ?: return@last
                    it.forEachLivingEntity {

                        val packet: PacketContainer = protocolManager.createPacket(PacketType.Play.Server.ATTACH_ENTITY)

                        packet.integers.write(0, holder).write(1, entityId)
                        Bukkit.getOnlinePlayers().forEach { player ->
                            try {
                                protocolManager.sendServerPacket(player, packet)
                            } catch (e: InvocationTargetException) {
                                throw RuntimeException(
                                    "Cannot send packet $packet", e
                                )
                            }
                        }

                    }
                }
            }
            return CompletableFuture.completedFuture(null)
        }
    }


    class ActionLeashClear(
        val selector: ParsedAction<*>?,
    ) : ScriptAction<Target.Container>() {

        override fun run(frame: ScriptFrame): CompletableFuture<Target.Container> {

            frame.containerOrSender(selector).thenAccept {
                val protocolManager = ProtocolLibrary.getProtocolManager()
                val packet: PacketContainer = protocolManager.createPacket(PacketType.Play.Server.ATTACH_ENTITY)

                val entity = it.firstLivingEntityTarget() ?: return@thenAccept

                packet.integers.write(0, entity.entityId).write(1, -1)

                Bukkit.getOnlinePlayers().forEach { player ->
                    try {
                        protocolManager.sendServerPacket(player, packet)
                    } catch (e: InvocationTargetException) {
                        throw RuntimeException(
                            "Cannot send packet $packet", e
                        )
                    }
                }
            }
            return CompletableFuture.completedFuture(null)
        }
    }


    companion object {

        /**
         *
         * 发包
         * 拴住           实体   被   实体
         * leash set [selector] [selector1]
         *
         * 发包解除拴绳
         * leash clear [selector]
         *
         */
        @KetherParser(["leash"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            it.switch {
                case("set") {
                    ActionLeashSet(it.nextParsedAction(), it.nextSelectorOrNull())
                }
                case("clear") {
                    ActionLeashClear(it.nextSelectorOrNull())
                }
            }
        }
    }

}
