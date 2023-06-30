package com.bh.planners.core.kether.game

import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.kether.*
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture


class ActionLeashHolder {

    class ActionLeashSet(
        val selector: ParsedAction<*>,
        val holder: ParsedAction<*>?,
    ) : ScriptAction<Void>() {

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            frame.createContainer(selector).thenAccept { container ->
                frame.containerOrSender(holder).thenAccept {
                    val holder = it.firstEntityTarget()
                    container.forEachLivingEntity {
                        setLeashHolder(holder)
                    }
                }
            }
            return CompletableFuture.completedFuture(null)
        }
    }


    class ActionLeashGet(
        val selector: ParsedAction<*>?,
    ) : ScriptAction<Target.Container>() {

        override fun run(frame: ScriptFrame): CompletableFuture<Target.Container> {
            val future = CompletableFuture<Target.Container>()
            frame.containerOrSender(selector).thenAccept {
                val entity = it.firstLivingEntityTarget() ?: return@thenAccept
                val container = Target.Container()
                if (entity.isLeashed) {
                    container.add(entity.leashHolder.toTarget())
                    future.complete(container)
                }
            }
            return future
        }
    }


    companion object {

        /**
         * 拴住      实体   被   实体
         * leash set selector1 [selector]
         *
         * 获取被谁拴住
         * leash get [selector]
         */
        @KetherParser(["leash"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            it.switch {
                case("set") {
                    ActionLeashSet(it.nextParsedAction(), it.nextSelectorOrNull())
                }
                case("get") {
                    ActionLeashGet(it.nextSelectorOrNull())
                }
            }
        }
    }

}
