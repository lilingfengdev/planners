package com.bh.planners.core.timer.impl

import com.bh.planners.core.timer.AbstractTimer
import com.bh.planners.core.timer.Template
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityShootBowEvent
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.kether.ScriptContext

object TimerShootBow : AbstractTimer<EntityShootBowEvent>() {
    override val name: String
        get() = "player shoot"
    override val eventClazz: Class<EntityShootBowEvent>
        get() = EntityShootBowEvent::class.java

    override fun check(e: EntityShootBowEvent): ProxyCommandSender? {
        val player = e.entity as? Player ?: return null
        return adaptPlayer(player)
    }

    /**
     * force 拉弓力度
     * shooter 拉弓者
     */
    override fun onStart(context: ScriptContext, template: Template, e: EntityShootBowEvent) {
        super.onStart(context, template, e)
        context.rootFrame().variables()["force"] = e.force
        context.rootFrame().variables()["shooter"] = e.entity.name
    }
}