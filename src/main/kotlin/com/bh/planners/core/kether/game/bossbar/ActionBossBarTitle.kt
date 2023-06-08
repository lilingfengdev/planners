package com.bh.planners.core.kether.game.bossbar

import taboolib.library.kether.ParsedAction
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.run
import taboolib.module.kether.str
import java.util.concurrent.CompletableFuture

class ActionBossBarTitle(val id: ParsedAction<*>, val value: ParsedAction<*>) : ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        frame.run(id).str {
            val bossbar = BossBarManager.getBossbar(it)
            frame.run(value).str { title ->
                bossbar?.title = title
                bossbar?.update()
            }
        }
        return CompletableFuture.completedFuture(null)
    }


}