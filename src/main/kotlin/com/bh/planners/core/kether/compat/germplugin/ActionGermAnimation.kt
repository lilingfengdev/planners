package com.bh.planners.core.kether.compat.germplugin

import com.bh.planners.core.kether.execEntity
import com.germ.germplugin.api.GermPacketAPI
import com.germ.germplugin.api.bean.AnimDataDTO
import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionGermAnimation(
    val state: String,
    val remove: Boolean,
    val speed: Float,
    val reverse: Boolean,
    val selector: ParsedAction<*>,
) : ScriptAction<Void>() {

    fun execute(entity: Entity, state: String, remove: Boolean, speed: Float, reverse: Boolean) {
        Bukkit.getOnlinePlayers().forEach {
            if (remove) {
                if (entity is Player) {
                    GermPacketAPI.sendBendClear(it, entity.entityId)
                } else {
                    GermPacketAPI.stopModelAnimation(it, entity.entityId, state)
                }
            } else {
                if (entity is Player) {
                    GermPacketAPI.sendBendAction(it, entity.entityId, AnimDataDTO(state, speed, reverse))
                } else {
                    GermPacketAPI.sendModelAnimation(it, entity.entityId, AnimDataDTO(state, speed, reverse))
                }
            }
        }
    }

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        frame.execEntity(selector) {
            execute(this, state, remove, speed, reverse)
        }
        return CompletableFuture.completedFuture(null)
    }
}