package com.bh.planners.core.kether.compat.germplugin

import com.bh.planners.core.kether.containerOrSender
import com.germ.germplugin.api.GermPacketAPI
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import taboolib.type.BukkitEquipment
import java.util.*
import java.util.concurrent.CompletableFuture

class ActionGermItemCooldown(val slot: ParsedAction<*>, val tick: ParsedAction<*>, val selector: ParsedAction<*>?) :
    ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        frame.run(slot).str {
            val equipment = BukkitEquipment.valueOf(it.uppercase(Locale.getDefault()))
            frame.run(tick).int { tick ->
                frame.containerOrSender(selector).thenAccept {
                    it.forEachPlayer {
                        GermPacketAPI.setItemStackCooldown(this, equipment.getItem(this), tick)
                    }
                }
            }
        }
        return CompletableFuture.completedFuture(null)
    }


}