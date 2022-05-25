package com.bh.planners.core.timer.impl

import com.bh.planners.core.timer.AbstractTimer
import com.bh.planners.core.timer.Template
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import taboolib.module.kether.ScriptContext

object TimerPlayerAttacked : AbstractTimer<EntityDamageByEntityEvent>() {
    override val name: String
        get() = "player attacked"
    override val eventClazz: Class<EntityDamageByEntityEvent>
        get() = EntityDamageByEntityEvent::class.java

    override fun check(e: EntityDamageByEntityEvent): Player? {
        return e.entity as? Player
    }


    override fun onStart(context: ScriptContext, template: Template, e: EntityDamageByEntityEvent) {
        super.onStart(context, template, e)
        context["damager"] = e.damager.name
        context["entity"] = e.entity.name
        context["cause"] = e.cause
        context["damage"] = e.damage
    }

}
