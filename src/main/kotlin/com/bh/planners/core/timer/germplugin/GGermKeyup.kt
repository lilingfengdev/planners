package com.bh.planners.core.timer.germplugin

import com.bh.planners.api.common.Plugin
import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.timer.AbstractTimer
import com.bh.planners.core.timer.Template
import com.germ.germplugin.api.event.GermKeyUpEvent
import taboolib.library.kether.ExitStatus
import taboolib.module.kether.ScriptContext

@Plugin("GermPlugin")
object GGermKeyup : AbstractTimer<GermKeyUpEvent>() {

    override val name: String
        get() = "germ key up"

    override val eventClazz: Class<GermKeyUpEvent>
        get() = GermKeyUpEvent::class.java

    override fun check(e: GermKeyUpEvent): Target {
        return e.player.toTarget()
    }

    override fun onStart(context: ScriptContext, template: Template, e: GermKeyUpEvent) {
        if (!condition(template, e)) {
            context.setExitStatus(ExitStatus.success())
        }
    }

    override fun condition(template: Template, event: GermKeyUpEvent): Boolean {
        val keys = template.keys
        return if (keys.isNotEmpty()) {
            keys.contains(event.keyType.name)
        } else true
    }


}