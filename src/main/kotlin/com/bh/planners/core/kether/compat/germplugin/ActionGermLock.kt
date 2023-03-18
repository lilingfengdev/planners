package com.bh.planners.core.kether.compat.germplugin

import com.bh.planners.core.kether.NAMESPACE
import com.bh.planners.core.kether.execPlayer
import com.bh.planners.core.kether.nextSelector
import com.germ.germplugin.api.GermPacketAPI
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionGermLock {

    class LockPlayer(val duration: ParsedAction<*>,val selector: ParsedAction<*>) : ScriptAction<Void>() {

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            frame.run(duration).long { duration ->
                frame.execPlayer(selector) {
                    GermPacketAPI.sendLockPlayerMove(this,duration)
                }
            }
            return CompletableFuture.completedFuture(null)
        }

    }

    class UnLockPlayer(val selector: ParsedAction<*>) : ScriptAction<Void>() {

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            frame.execPlayer(selector) {
                GermPacketAPI.sendUnlockPlayerMove(this)
            }
            return CompletableFuture.completedFuture(null)
        }

    }

    companion object {

        /**
         * 锁定移动（客户端行为，不会产生抽搐） 依赖Germ
         * move lock [duration] [they selector]
         * move unlock [they selector]
         */
        @KetherParser(["move"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            it.switch {
                case("lock") {
                    LockPlayer(it.nextParsedAction(),it.nextSelector())
                }
                case("unlock") {
                    UnLockPlayer(it.nextSelector())
                }
            }
        }

    }

}