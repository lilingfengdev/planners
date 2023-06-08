package com.bh.planners.core.kether.compat

import com.bh.planners.core.kether.*
import com.bh.planners.core.kether.compat.attribute.AttributeBridge
import org.bukkit.entity.LivingEntity
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionAttribute {

    class AttributeAdd(
        val source: ParsedAction<*>,
        val timeout: ParsedAction<*>,
        val list: ParsedAction<*>,
        val selector: ParsedAction<*>?,
    ) : ScriptAction<Void>() {

        fun execute(entity: LivingEntity, source: String, timeout: Long, list: List<String>) {
            AttributeBridge.INSTANCE?.addAttributes(source, entity.uniqueId, timeout * 50, list)
        }

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {

            frame.run(source).str { source ->
                frame.run(timeout).long { tick ->
                    frame.run(list).str {
                        val list = it.split(",")
                        frame.containerOrSender(selector).thenAccept {
                            it.forEachLivingEntity { execute(this, source, tick, list) }
                        }
                    }
                }
            }
            return CompletableFuture.completedFuture(null)
        }
    }

    class AttributeTake(val source: ParsedAction<*>, val selector: ParsedAction<*>?) : ScriptAction<Void>() {
        fun execute(entity: LivingEntity, source: String) {
            AttributeBridge.INSTANCE?.removeAttributes(entity.uniqueId, source)
            AttributeBridge.INSTANCE?.update(entity.uniqueId)
        }

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            frame.run(source).str { source ->
                frame.containerOrSender(selector).thenAccept {
                    it.forEachLivingEntity { execute(this, source) }
                }
            }
            return CompletableFuture.completedFuture(null)
        }
    }

    class AttributeUpdate(val selector: ParsedAction<*>?) : ScriptAction<Void>() {

        fun execute(entity: LivingEntity) {
            AttributeBridge.INSTANCE?.update(entity.uniqueId)
        }

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            frame.containerOrSender(selector).thenAccept {
                it.forEachLivingEntity { execute(this) }
            }
            return CompletableFuture.completedFuture(null)
        }
    }

    class AttributeGet(val selector: ParsedAction<*>) : ScriptAction<Any>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Any> {
            TODO("Not yet implemented")
        }

    }

    companion object {

        /**
         * attribute [add/+=] [source] [timeout] [attribute: action(,分割)] [selector]
         * test: attribute add def0 60000 "攻击力 +10,生命 +20" they "@self"
         *
         * attribute [take/-=] [source] [selector]
         * test: attribute take def0 they  "@self"
         *
         * attribute update [selector]
         * test: attribute update they  "@self"
         */
        @KetherParser(["attribute"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            it.switch {
                case("add", "+=") {
                    AttributeAdd(
                        it.nextParsedAction(),
                        it.nextParsedAction(),
                        it.nextParsedAction(),
                        it.nextSelectorOrNull()
                    )
                }
                case("take", "-=") {
                    AttributeTake(it.nextParsedAction(), it.nextSelectorOrNull())
                }
                case("update", "refresh") {
                    AttributeUpdate(it.nextSelectorOrNull())
                }
                other {
                    AttributeGet(it.nextSelector())
                }
            }
        }

    }


}