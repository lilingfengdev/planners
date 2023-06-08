package com.bh.planners.core.kether.game.bossbar

import taboolib.library.kether.ParsedAction
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.run
import taboolib.module.kether.str
import java.util.concurrent.CompletableFuture

class ActionBossBarDelete(val id: ParsedAction<*>) : ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        frame.run(id).str {
            BossBarManager.removeBossbar(it)
        }
        return CompletableFuture.completedFuture(null)
    }


}