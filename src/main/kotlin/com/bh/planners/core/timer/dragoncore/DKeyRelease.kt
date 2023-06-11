package com.bh.planners.core.timer.dragoncore

import com.bh.planners.api.common.Plugin
import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.timer.AbstractTimer
import com.bh.planners.core.timer.Template
import eos.moe.dragoncore.api.event.KeyReleaseEvent
import taboolib.library.kether.ExitStatus
import taboolib.module.kether.ScriptContext

@Plugin("DragonCore")
object DKeyRelease : AbstractTimer<KeyReleaseEvent>() {

    override val name: String
        get() = "dragon key release"

    override val eventClazz: Class<KeyReleaseEvent>
        get() = KeyReleaseEvent::class.java

    override fun check(e: KeyReleaseEvent): Target {
        return e.player.toTarget()
    }

    private fun Template.keyId(): List<String> {
        return this.root.getStringList("__option__.key")
    }

    override fun onStart(context: ScriptContext, template: Template, e: KeyReleaseEvent) {
        if (!condition(template, e)) {
            context.setExitStatus(ExitStatus.success())
        }
    }

    override fun condition(template: Template, event: KeyReleaseEvent): Boolean {
        val keyId = template.keyId()
        return if (keyId.isNotEmpty()) {
            keyId.contains(event.key)
        } else true
    }


}