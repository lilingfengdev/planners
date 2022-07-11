package com.bh.planners.core.kether.compat.germplugin

import com.bh.planners.core.kether.NAMESPACE
import com.bh.planners.core.kether.compat.adyeshach.AdyeshachEntity
import com.bh.planners.core.kether.execEntity
import com.bh.planners.core.kether.selectorAction
import com.germ.germplugin.api.GermPacketAPI
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.manager.Manager
import ink.ptms.adyeshach.common.script.ScriptHandler.getEntities
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionGermEngine {

    class ActionAnimation(val state: String, val remove: Boolean, val selector: ParsedAction<*>) : ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            frame.execEntity(selector) {
                if (this is AdyeshachEntity) {
                    this.entity.forViewers { p ->
                        if (remove) {
                            GermPacketAPI.stopModelAnimation(p, this.entity.index, state)
                        } else {
                            GermPacketAPI.sendModelAnimation(p, this.entity.index, state)
                        }
                    }
                }
            }
            return CompletableFuture.completedFuture(null)
        }
    }

    companion object {

        /**
         * germ animation send [name: token] [selector]
         * germ animation stop [name: token] [selector]
         */
        @KetherParser(["germengine", "germ", "germplugin"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            it.switch {
                case("animation") {
                    when (it.expects("send", "stop")) {
                        "send" -> {
                            ActionAnimation(it.nextToken(), false, it.selectorAction() ?: error("the lack of 'they' cite target"))
                        }

                        "stop" -> {
                            ActionAnimation(it.nextToken(), true, it.selectorAction() ?: error("the lack of 'they' cite target"))
                        }

                        else -> error("out of case")
                    }
                }
            }
        }
    }
}