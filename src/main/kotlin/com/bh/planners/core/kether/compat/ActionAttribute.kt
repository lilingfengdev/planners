package com.bh.planners.core.kether.compat

import com.bh.planners.core.kether.compat.attribute.AttributeBridge
import com.bh.planners.core.kether.*
import org.bukkit.entity.LivingEntity
import taboolib.common.util.asList
import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.UUID
import java.util.concurrent.CompletableFuture

class ActionAttribute {

    class AttributeAdd(val source: ParsedAction<*>, val timeout: ParsedAction<*>, val list: ParsedAction<*>, val selector: ParsedAction<*>?) :
        ScriptAction<Void>() {

        fun execute(entity: LivingEntity, source: String, timeout: Long, list: List<String>) {
            AttributeBridge.INSTANCE?.addAttributes(source, entity.uniqueId, timeout * 50, list)
        }

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {

            return frame.newFrame(source).run<Any>().thenAccept {
                val source = it.toString()
                frame.newFrame(timeout).run<Any>().thenAccept {
                    val timeout = Coerce.toLong(it)
                    frame.newFrame(list).run<Any>().thenAccept {
                        val list = it.toString().split(",")
                        if (selector != null) {
                            frame.execLivingEntity(selector) { execute(this, source, timeout, list) }
                        } else {
                            execute(frame.bukkitPlayer() ?: return@thenAccept, source, timeout, list)
                        }
                    }
                }
            }
        }
    }

    class AttributeTake(val source: ParsedAction<*>, val selector: ParsedAction<*>?) :
        ScriptAction<Void>() {
        fun execute(entity: LivingEntity, source: String) {
            AttributeBridge.INSTANCE?.removeAttributes(entity.uniqueId, source)
            AttributeBridge.INSTANCE?.update(entity.uniqueId)
        }

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            return frame.newFrame(source).run<Any>().thenAccept {
                val source = it.toString()
                if (selector != null) {
                    frame.execLivingEntity(selector) { execute(this, source) }
                } else {
                    execute(frame.bukkitPlayer() ?: return@thenAccept, source)
                }
            }
        }
    }

    class AttributeUpdate(val selector: ParsedAction<*>?) : ScriptAction<Void>() {

        fun execute(entity: LivingEntity) {
            AttributeBridge.INSTANCE?.update(entity.uniqueId)
        }

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            if (selector != null) {
                frame.execLivingEntity(selector) { execute(this) }
            } else {
                execute(frame.bukkitPlayer() ?: return CompletableFuture.completedFuture(null))
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
         * test: attribute add def0 60000 "攻击力 +10,生命 +20" they "-@self"
         *
         * attribute [take/-=] [source] [selector]
         * test: attribute take def0 they  "-@self"
         *
         * attribute update [selector]
         * test: attribute update they  "-@self"
         */
        @KetherParser(["attribute"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            it.switch {
                case("add", "+=") {
                    AttributeAdd(
                        it.nextParsedAction(),
                        it.nextParsedAction(),
                        it.nextParsedAction(),
                        it.selectorAction()
                    )
                }
                case("take", "-=") {
                    AttributeTake(it.nextParsedAction(), it.selectorAction())
                }
                case("update", "refresh") {
                    AttributeUpdate(it.selectorAction())
                }
                other {
                    AttributeGet(it.selector())
                }
            }
        }

    }


}