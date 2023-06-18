package com.bh.planners.core.kether.compat.dragoncore

import com.bh.planners.core.kether.execPlayer
import com.bh.planners.core.kether.readAccept
import eos.moe.dragoncore.api.CoreAPI
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import java.util.concurrent.CompletableFuture

class ActionDragonWorldTextureRemove(
    val key: ParsedAction<*>,
    val selector: ParsedAction<*>,
) : ScriptAction<Void>() {


    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        frame.readAccept<String>(key) { key ->
            frame.execPlayer(selector) {
                CoreAPI.removePlayerWorldTexture(this, key)
            }
        }
        return CompletableFuture.completedFuture(null)
    }

}