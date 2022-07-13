package com.bh.planners.core.kether

import com.bh.planners.core.kether.event.ActionEventLoader
import org.bukkit.event.Event
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*

class ActionEvent {

    companion object {

        fun ScriptFrame.event(): Event {
            return rootVariables().get<Event>("@Event").orElse(null) ?: error("Error running environment !")
        }

        /**
         * 事件取消
         * event cancel [to [false/true]]
         *
         */
        @KetherParser(["event"], namespace = NAMESPACE, shared = true)
        fun <T> parser() = scriptParser {
            val strings = ActionEventLoader.actions.keys.flatMap { it.toList() }
            val expects = it.expects(*strings.toTypedArray())
            val actionParser = ActionEventLoader.getAction(expects)
            actionParser.resolve<T>(it)
        }

    }

}