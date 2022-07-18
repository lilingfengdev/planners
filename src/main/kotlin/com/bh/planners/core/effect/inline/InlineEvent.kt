package com.bh.planners.core.effect.inline

import com.bh.planners.core.kether.namespaces
import com.bh.planners.core.pojo.Session
import com.bh.planners.core.pojo.Skill
import org.bukkit.entity.LivingEntity
import taboolib.common.platform.function.submit
import taboolib.module.kether.KetherShell
import taboolib.module.kether.ScriptContext
import taboolib.module.kether.printKetherErrorMessage

interface InlineEvent {

    val name: String

    fun inject(context: ScriptContext)

    companion object {

        fun Session.callEvent(name: String, owner: LivingEntity = executor.origin as LivingEntity, event: InlineEvent) {
            val processor = getEventProcessor(name) ?: return
            if (processor.async) {
                submit(async = true) { run(event,owner, processor) }
            } else {
                run(event,owner, processor)
            }
        }

        fun Session.run(event: InlineEvent,owner: LivingEntity, eventProcessor: Skill.EventProcessor) {
            try {
                KetherShell.eval(eventProcessor.action, sender = executor, namespace = namespaces) {
                    rootFrame().variables()["@Session"] = this@run
                    rootFrame().variables()["@Skill"] = playerSkill
                    rootFrame().variables()["@Owner"] = owner
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