package com.bh.planners.core.skill.effect.inline

import com.bh.planners.core.kether.namespaces
import com.bh.planners.core.pojo.Session
import com.bh.planners.core.pojo.Skill
import taboolib.common.platform.function.submit
import taboolib.module.kether.KetherShell
import taboolib.module.kether.ScriptContext
import taboolib.module.kether.printKetherErrorMessage

interface InlineEvent {

    val name: String

    fun inject(context: ScriptContext)

    companion object {

        fun Session.callEvent(name: String, event: InlineEvent) {
            val processor = getEventProcessor(name) ?: return
            if (processor.async) {
                submit(async = true) { run(event, processor) }
            } else {
                run(event, processor)
            }
        }

        fun Session.run(event: InlineEvent, eventProcessor: Skill.EventProcessor) {
            try {
                KetherShell.eval(eventProcessor.action, sender = executor, namespace = namespaces) {
                    rootFrame().variables()["@Session"] = this@run
                    rootFrame().variables()["@Skill"] = playerSkill
                    variables.forEach {
                        rootFrame().variables()[it.key] = it.value
                    }
                    event.inject(this)
                }
            } catch (e: Throwable) {
                e.printKetherErrorMessage()
            }

        }

        fun Session.getEventProcessor(name: String): Skill.EventProcessor? {
            return skill.events.firstOrNull { it.id == name }
        }

    }


}