package com.bh.planners.core.timer.germplugin

import com.bh.planners.api.common.Plugin
import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.timer.AbstractTimer
import com.bh.planners.core.timer.Template
import com.germ.germplugin.api.event.GermKeyUpEvent

@Plugin("GermPlugin")
object GGermKeyup : AbstractTimer<GermKeyUpEvent>() {

    override val name: String
        get() = "germ key up"

    override val eventClazz: Class<GermKeyUpEvent>
        get() = GermKeyUpEvent::class.java

    override fun check(e: GermKeyUpEvent): Target {
        return e.player.toTarget()
    }

    private fun Template.keyId(): String? {
        return this.root.getString("__option__.key")
    }

    override fun condition(template: Template, event: GermKeyUpEvent): Boolean {
        val keyId = template.keyId()
        return if (keyId != null) {
            keyId == event.keyType.name
        } else true
    }


}