package com.bh.planners.core.kether.compat

import com.bh.planners.core.kether.NAMESPACE
import com.bh.planners.core.kether.asPlayer
import com.bh.planners.core.kether.execEntity
import com.bh.planners.core.kether.selectorAction
import ink.ptms.adyeshach.api.AdyeshachAPI
import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.chat.colored
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

/**
 * Chemdah
 * ink.ptms.chemdah.module.kether.conversation.ConversationTalkPlayer
 *
 * @author sky
 * @since 2021/2/10 6:39 下午
 */
class ActionTalkEntity(val action: ParsedAction<*>, val selector: ParsedAction<*>?) : ScriptAction<Void>() {

    fun execute(entity: Entity, message: String) {
        entity.world.players.forEach { player ->
            message.replace("@Target", entity.name).split("\\n").colored().forEachIndexed { index, s ->

                AdyeshachAPI.createHolographic(
                    player,
                    entity.location.clone().add(0.0, entity.height + 0.25 + (index * 0.3), 0.0),
                    40,
                    { it },
                    "§7$s"
                )
            }
        }

    }

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        frame.newFrame(action).run<Any>().thenAccept {
            if (selector != null) {
                frame.execEntity(selector) { execute(this, it.toString()) }
            } else {
                execute(frame.asPlayer() ?: return@thenAccept, it.toString())
            }
        }

        return CompletableFuture.completedFuture(null)
    }

    companion object {

        @KetherParser(["talk"], namespace = NAMESPACE)
        fun parser() = scriptParser {
            ActionTalkEntity(it.next(ArgTypes.ACTION), it.selectorAction())
        }
    }
}