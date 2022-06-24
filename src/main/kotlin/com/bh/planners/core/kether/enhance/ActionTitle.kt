package com.bh.planners.core.kether.enhance

import com.bh.planners.core.kether.*
import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.library.kether.QuestContext
import taboolib.library.kether.actions.LiteralAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionTitle(
    val title: ParsedAction<*>,
    val subTitle: ParsedAction<*>,
    val fadeIn: ParsedAction<*>,
    val stay: ParsedAction<*>,
    val fadeOut: ParsedAction<*>,
    val selector: ParsedAction<*>
) : ScriptAction<Void>() {

    override fun run(frame: QuestContext.Frame): CompletableFuture<Void> {
        return frame.newFrame(title).run<Any>().thenAccept { t ->
            frame.newFrame(subTitle).run<Any>().thenAccept { s ->
                frame.newFrame(fadeIn).run<Any>().thenAccept { fadeIn ->
                    frame.newFrame(stay).run<Any>().thenAccept { stay ->
                        frame.newFrame(fadeOut).run<Any>().thenAccept { fadeOut ->

                            val title = t.toString()
                            val subTitle = s.toString()
                            frame.createTargets(selector).thenAccept {
                                it.forEachPlayer {
                                    sendTitle(
                                        title,
                                        subTitle,
                                        Coerce.toInteger(fadeIn),
                                        Coerce.toInteger(stay),
                                        Coerce.toInteger(fadeOut)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    internal object Parser {

        @KetherParser(["title"], namespace = NAMESPACE)
        fun parser() = scriptParser {
            val title = it.next(ArgTypes.ACTION)
            it.mark()
            val subTitle = try {
                it.expect("subtitle")
                it.next(ArgTypes.ACTION)
            } catch (ignored: Exception) {
                it.reset()
                ParsedAction(LiteralAction<String>(""))
            }
            var fadeIn: ParsedAction<*> = ParsedAction(LiteralAction<String>("0"))
            var stay: ParsedAction<*> = ParsedAction(LiteralAction<String>("20"))
            var fadeOut: ParsedAction<*> = ParsedAction(LiteralAction<String>("0"))
            it.mark()
            try {
                it.expects("by", "with")
                fadeIn = it.next(ArgTypes.ACTION)
                stay = it.next(ArgTypes.ACTION)
                fadeOut = it.next(ArgTypes.ACTION)
            } catch (ignored: Exception) {
                it.reset()
            }
            ActionTitle(title, subTitle, fadeIn, stay, fadeOut, it.next(ArgTypes.ACTION))
        }
    }
}