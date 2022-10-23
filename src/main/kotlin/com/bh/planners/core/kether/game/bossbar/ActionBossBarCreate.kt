package com.bh.planners.core.kether.game.bossbar

import com.bh.planners.util.generatorId
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.UUID
import java.util.concurrent.CompletableFuture

class ActionBossBarCreate(val tick: ParsedAction<*>) : ScriptAction<String>() {

    override fun run(frame: ScriptFrame): CompletableFuture<String> {
        val future = CompletableFuture<String>()
        frame.run(tick).long { tick ->
            val bossbar = BossBarManager.createBossbar(generatorId().toString(), tick)
            future.complete(bossbar.id)
        }
        return future
    }


}