package com.bh.planners.core.kether.compat.mythicmobs

import com.bh.planners.core.kether.containerOrSender
import io.lumine.xikage.mythicmobs.MythicMobs
import io.lumine.xikage.mythicmobs.util.MythicUtil
import org.bukkit.entity.Player
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import java.util.concurrent.CompletableFuture

class ActionMythicCast(
    val skill: String,
    val power: Float,
    val selector: ParsedAction<*>?,
) : ScriptAction<Void>() {

    fun execute(sender: Player) {
        val targets = MythicUtil.getTargetedEntity(sender)
        MythicMobs.inst().apiHelper.castSkill(
            sender,
            skill,
            sender,
            sender.location,
            listOf(targets),
            listOf(),
            power
        )
    }

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        frame.containerOrSender(selector).thenAccept {
            it.forEachPlayer {
                execute(this)
            }
        }
        return CompletableFuture.completedFuture(null)
    }

}