package com.bh.planners.core.kether.compat

import com.bh.planners.core.feature.attribute.AttributeBridge
import com.bh.planners.core.kether.NAMESPACE
import com.bh.planners.core.kether.createTargets
import taboolib.common.util.asList
import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionAttribute {

    class AttributeAdd(
        val source: ParsedAction<*>,
        val timeout: ParsedAction<*>,
        val list: ParsedAction<*>,
        val selector: ParsedAction<*>
    ) :
        ScriptAction<String>() {
        override fun run(frame: ScriptFrame): CompletableFuture<String> {
            val future = CompletableFuture<String>()
            frame.newFrame(source).run<Any>().thenAccept { source ->
                frame.newFrame(timeout).run<Any>().thenAccept { timeout ->
                    frame.newFrame(list).run<Any>().thenAccept { list ->
                        frame.createTargets(selector).thenAccept { selector ->
                            selector.forEachEntity {
                                AttributeBridge.INSTANCE?.addAttributes(
                                    source.toString(),
                                    uniqueId,
                                    Coerce.toLong(timeout),
                                    list.asList()
                                )
                                AttributeBridge.INSTANCE?.update(uniqueId)
                            }
                            future.complete(source.toString())
                        }
                    }
                }
            }
            return future
        }
    }

    class AttributeTake(val source: ParsedAction<*>, val selector: ParsedAction<*>) :
        ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            frame.newFrame(source).run<Any>().thenAccept { source ->
                frame.createTargets(selector).thenAccept { selector ->
                    selector.forEachEntity {
                        AttributeBridge.INSTANCE?.removeAttributes(uniqueId, source.toString())
                        AttributeBridge.INSTANCE?.update(uniqueId)
                    }
                }
            }
            return CompletableFuture.completedFuture(null)
        }
    }

    class AttributeUpdate(val selector: ParsedAction<*>) :
        ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            frame.createTargets(selector).thenAccept { selector ->
                selector.forEachEntity {
                    AttributeBridge.INSTANCE?.update(uniqueId)
                }
            }
            return CompletableFuture.completedFuture(null)
        }
    }

    companion object {

        /**
         * attribute [add/+=] [source] [timeout] [attribute list] [selector]
         * test: attribute add def0 60000 [ "攻击力 +10" ] "-@self"
         *
         * attribute [take/-=] [source] [selector]
         * test: attribute take def0 "-@self"
         *
         * attribute update [selector]
         * test: attribute update "-@self"
         */
        @KetherParser(["attribute"], namespace = NAMESPACE)
        fun parser() = scriptParser {
            it.switch {
                case("add", "+=") {
                    AttributeAdd(
                        it.next(ArgTypes.ACTION),
                        it.next(ArgTypes.ACTION),
                        it.next(ArgTypes.ACTION),
                        it.next(ArgTypes.ACTION)
                    )
                }
                case("take", "-=") {
                    AttributeTake(it.next(ArgTypes.ACTION), it.next(ArgTypes.ACTION))
                }
                case("update", "refresh") {
                    AttributeUpdate(it.next(ArgTypes.ACTION))
                }
            }
        }

    }


}