package com.bh.planners.core.kether.game.bossbar

import com.bh.planners.core.kether.runTransfer
import org.bukkit.boss.BarStyle
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.run
import taboolib.module.kether.str
import java.util.concurrent.CompletableFuture

class ActionBossBarStyle(val id: ParsedAction<*>, val value: ParsedAction<*>) : ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        frame.run(id).str {
            val bossbar = BossBarManager.getBossbar(it)
            frame.runTransfer<BarStyle>(value).thenAccept { style ->
                bossbar?.style = style
                bossbar?.update()
            }
        }
        return CompletableFuture.completedFuture(null)
    }


}