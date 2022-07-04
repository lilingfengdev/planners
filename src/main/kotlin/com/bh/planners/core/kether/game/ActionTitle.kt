package com.bh.planners.core.kether.game

import com.bh.planners.core.kether.*
import org.bukkit.entity.Player
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
    val selector: ParsedAction<*>?
) : ScriptAction<Void>() {

    fun execute(player: Player, t: String, s: String, fadeIn: Int, stay: Int, fadeOut: Int) {
        player.sendTitle(t, s, fadeIn, stay, fadeOut)
    }

    override fun run(frame: QuestContext.Frame): CompletableFuture<Void> {
        return frame.newFrame(title).run<Any>().thenAccept {
            val title = it.toString()
            frame.newFrame(subTitle).run<Any>().thenAccept { s ->
                val subTitle = it.toString()
                frame.newFrame(fadeIn).run<Any>().thenAccept {
                    val fadeIn = Coerce.toInteger(it)
                    frame.newFrame(stay).run<Any>().thenAccept {
                        val stay = Coerce.toInteger(it)
                        frame.newFrame(fadeOut).run<Any>().thenAccept {
                            val fadeOut = Coerce.toInteger(it)
                            if (selector != null) {
                                frame.execPlayer(selector) { execute(this, title, subTitle, fadeIn, stay, fadeOut) }
                            } else {
                                execute(frame.asPlayer()!!, title, subTitle, fadeIn, stay, fadeOut)
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
            ActionTitle(title, subTitle, fadeIn, stay, fadeOut, it.selectorAction())
        }
    }
}