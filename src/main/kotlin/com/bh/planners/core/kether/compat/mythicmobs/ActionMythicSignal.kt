package com.bh.planners.core.kether.compat.mythicmobs

import com.bh.planners.core.kether.containerOrSender
import com.bh.planners.core.kether.senderPlannerProfile
import io.lumine.xikage.mythicmobs.MythicMobs
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter
import org.bukkit.entity.Player
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import java.util.UUID
import java.util.concurrent.CompletableFuture

class ActionMythicSignal(
    val signal: String,
    val selector: ParsedAction<*>?,
) : ScriptAction<Void>() {

    fun execute(signal: String, target: UUID, sender: Player) {
        MythicMobs.inst().mobManager.getActiveMob(target).get().signalMob(BukkitAdapter.adapt(sender), signal)
    }

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        frame.containerOrSender(selector).thenAccept {
            val player = frame.senderPlannerProfile()?.player ?: error("没发送者")
            it.forEachEntity {
                execute(signal, this.uniqueId, player)
            }
        }
        return CompletableFuture.completedFuture(null)
    }

}