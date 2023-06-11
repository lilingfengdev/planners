package com.bh.planners.core.timer.dragoncore

import com.bh.planners.api.common.Plugin
import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.timer.AbstractTimer
import com.bh.planners.core.timer.Template
import eos.moe.dragoncore.api.event.KeyPressEvent
import taboolib.library.kether.ExitStatus
import taboolib.module.kether.ScriptContext

@Plugin("DragonCore")
object DKeyPress : AbstractTimer<KeyPressEvent>() {

    override val name: String
        get() = "dragon key press"

    override val eventClazz: Class<KeyPressEvent>
        get() = KeyPressEvent::class.java

    override fun check(e: KeyPressEvent): Target {
        return e.player.toTarget()
    }

    override fun onStart(context: ScriptContext, template: Template, e: KeyPressEvent) {
        if (!condition(template, e)) {
            context.setExitStatus(ExitStatus.success())
        }
    }

    override fun condition(template: Template, event: KeyPressEvent): Boolean {
        val keys = template.keys
        return if (keys.isNotEmpty()) {
            keys.contains(event.key)
        } else true
    }

}