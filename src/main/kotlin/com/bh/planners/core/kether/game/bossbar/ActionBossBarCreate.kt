package com.bh.planners.core.kether.game.bossbar

import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionBossBarCreate(val id: ParsedAction<*>, val tick: ParsedAction<*>) : ScriptAction<String>() {

    override fun run(frame: ScriptFrame): CompletableFuture<String> {
        val future = CompletableFuture<String>()
        frame.run(id).str { id ->
            frame.run(tick).long { tick ->
                val bossbar = BossBarManager.createBossbar(id, tick)
                future.complete(bossbar.id)
            }
        }
        return future
    }


}