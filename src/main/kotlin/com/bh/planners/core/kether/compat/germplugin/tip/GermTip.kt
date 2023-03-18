package com.bh.planners.core.kether.compat.germplugin.tip

import com.bh.planners.core.kether.*
import com.gitee.war.module.germ.GermTip
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class GermTip {

    class Send(val message: ParsedAction<*>, val selector: ParsedAction<*>?) : ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {

            frame.run(message).str { message ->
                frame.containerOrSender(selector).thenAccept {
                    it.forEachPlayer { GermTip.send(this,message) }
                }
            }

            return CompletableFuture.completedFuture(null)
        }

    }

    companion object {

        @KetherParser(["tip"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            Send(it.nextParsedAction(), it.nextSelectorOrNull())
        }

    }


}