package com.bh.planners.core.timer.dragoncore

import com.bh.planners.api.common.Plugin
import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.timer.AbstractTimer
import com.bh.planners.core.timer.Template
import eos.moe.dragoncore.api.event.KeyPressEvent

@Plugin("DragonCore")
object DKeyPress : AbstractTimer<KeyPressEvent>() {

    override val name: String
        get() = "dragon key press"

    override val eventClazz: Class<KeyPressEvent>
        get() = KeyPressEvent::class.java

    override fun check(e: KeyPressEvent): Target {
        return e.player.toTarget()
    }

    private fun Template.keyId(): String? {
        return this.root.getString("__option__.key")
    }

    override fun condition(template: Template, event: KeyPressEvent): Boolean {
        val keyId = template.keyId()
        return if (keyId != null) {
            keyId == event.key
        } else true
    }

}