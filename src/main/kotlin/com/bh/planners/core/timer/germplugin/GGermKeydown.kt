package com.bh.planners.core.timer.germplugin

import com.bh.planners.api.common.Plugin
import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.timer.AbstractTimer
import com.bh.planners.core.timer.Template
import com.germ.germplugin.api.event.GermKeyDownEvent
import taboolib.library.kether.ExitStatus
import taboolib.module.kether.ScriptContext

@Plugin("GermPlugin")
object GGermKeydown : AbstractTimer<GermKeyDownEvent>() {

    override val name: String
        get() = "germ key down"

    override val eventClazz: Class<GermKeyDownEvent>
        get() = GermKeyDownEvent::class.java

    override fun check(e: GermKeyDownEvent): Target {
        return e.player.toTarget()
    }

    private fun Template.keyId(): List<String> {
        return this.root.getStringList("__option__.key").map { it.toUpperCase() }
    }

    override fun onStart(context: ScriptContext, template: Template, e: GermKeyDownEvent) {
        if (!condition(template, e)) {
            context.setExitStatus(ExitStatus.success())
        }
    }

    override fun condition(template: Template, event: GermKeyDownEvent): Boolean {
        val keyId = template.keyId()
        return if (keyId.isNotEmpty()) {
            keyId.contains(event.keyType.name)
        } else true
    }


}