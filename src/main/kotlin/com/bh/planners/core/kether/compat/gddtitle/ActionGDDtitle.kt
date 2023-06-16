package com.bh.planners.core.kether.compat.gddtitle

import com.bh.planners.core.kether.NAMESPACE
import com.bh.planners.core.kether.containerOrSender
import com.bh.planners.core.kether.nextSelectorOrNull
import me.goudan.gddtitle.api.GDDTitleAPI
import org.bukkit.entity.Player
import taboolib.common5.Coerce
import taboolib.library.kether.ParsedAction
import taboolib.library.kether.QuestContext
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture


class ActionGDDtitle(
    val title: ParsedAction<*>,
    val fadeIn: ParsedAction<*>,
    val stay: ParsedAction<*>,
    val fadeOut: ParsedAction<*>,
    val selector: ParsedAction<*>?,
) : ScriptAction<Void>() {


    fun execute(player: Player, title: String, fadeIn: Int, stay: Int, fadeOut: Int) {
        GDDTitleAPI.sendTitle(player, title, fadeIn, stay, fadeOut)
    }

    override fun run(frame: QuestContext.Frame): CompletableFuture<Void> {
        return frame.newFrame(title).run<Any>().thenAccept { title ->
            frame.newFrame(fadeIn).run<Any>().thenAccept { fadeIn ->
                frame.newFrame(stay).run<Any>().thenAccept { stay ->
                    frame.newFrame(fadeOut).run<Any>().thenAccept { fadeOut ->
                        frame.containerOrSender(selector).thenAccept {
                            it.forEachPlayer { execute(this, title.toString(), Coerce.toInteger(fadeIn), Coerce.toInteger(stay), Coerce.toInteger(fadeOut)) }
                        }
                    }
                }
            }
        }
    }



    companion object Parser {

        @KetherParser(["gddtitle"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            val title = it.nextParsedAction()
            it.mark()
            var fadeIn: ParsedAction<*> = literalAction(20)
            var stay: ParsedAction<*> = literalAction(20)
            var fadeOut: ParsedAction<*> = literalAction(20)
            it.mark()
            try {
                it.expects("by", "with")
                fadeIn = it.nextParsedAction()
                stay = it.nextParsedAction()
                fadeOut = it.nextParsedAction()
            } catch (ignored: Exception) {
                it.reset()
            }
            ActionGDDtitle(title, fadeIn, stay, fadeOut, it.nextSelectorOrNull())
        }

    }

}


