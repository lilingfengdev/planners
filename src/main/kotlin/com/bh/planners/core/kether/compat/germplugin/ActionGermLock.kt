package com.bh.planners.core.kether.compat.germplugin

import com.bh.planners.core.kether.execPlayer
import com.germ.germplugin.api.GermPacketAPI
import com.germ.germplugin.api.ViewType
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionGermLock {

    class LockPlayerMove(val duration: ParsedAction<*>, val selector: ParsedAction<*>) : ScriptAction<Void>() {

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            frame.run(duration).long { duration ->
                frame.execPlayer(selector) {
                    GermPacketAPI.sendLockPlayerMove(this, duration)
                }
            }
            return CompletableFuture.completedFuture(null)
        }

    }

    class UnLockPlayerMove(val selector: ParsedAction<*>) : ScriptAction<Void>() {

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            frame.execPlayer(selector) {
                GermPacketAPI.sendUnlockPlayerMove(this)
            }
            return CompletableFuture.completedFuture(null)
        }

    }

    class LockPlayerView(val duration: ParsedAction<*>, val selector: ParsedAction<*>) : ScriptAction<Void>() {

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            frame.run(duration).long { duration ->
                frame.execPlayer(selector) {
                    GermPacketAPI.sendLockPlayerCameraRotate(this, duration)
                }
            }
            return CompletableFuture.completedFuture(null)
        }

    }

    class UnLockPlayerView(val selector: ParsedAction<*>) : ScriptAction<Void>() {

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            frame.execPlayer(selector) {
                GermPacketAPI.sendUnlockPlayerCameraRotate(this)
            }
            return CompletableFuture.completedFuture(null)
        }

    }

    class LockPlayerViewType(val duration: ParsedAction<*>, val type: ParsedAction<*>, val selector: ParsedAction<*>) : ScriptAction<Void>() {

        val typelist = listOf(ViewType.FIRST_PERSON, ViewType.THIRD_PERSON, ViewType.THIRD_PERSON_REVERSE)

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            frame.run(duration).long { duration ->
                frame.run(type).int { type ->
                    frame.execPlayer(selector) {
                        GermPacketAPI.sendLockPlayerCameraView(this, typelist[type], duration)
                    }
                }
            }
            return CompletableFuture.completedFuture(null)
        }

    }

    class UnLockPlayerViewType(val selector: ParsedAction<*>) : ScriptAction<Void>() {

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            frame.execPlayer(selector) {
                GermPacketAPI.sendUnlockPlayerCameraView(this)
            }
            return CompletableFuture.completedFuture(null)
        }

    }

}