package com.bh.planners.core.timer.germplugin

import com.bh.planners.api.common.Plugin
import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.timer.AbstractTimer
import com.bh.planners.core.timer.Template
import eos.moe.dragoncore.api.event.KeyReleaseEvent

@Plugin("DragonCore")
class DKeyRelease : AbstractTimer<KeyReleaseEvent>() {

    override val name: String
        get() = "dragon key release"

    override val eventClazz: Class<KeyReleaseEvent>
        get() = KeyReleaseEvent::class.java

    override fun check(e: KeyReleaseEvent): Target {
        return e.player.toTarget()
    }

    private fun Template.keyId(): String? {
        return this.root.getString("__option__.key")
    }

    override fun condition(template: Template, event: KeyReleaseEvent): Boolean {
        val keyId = template.keyId()
        return if (keyId != null) {
            keyId == event.key
        } else true

    }


}