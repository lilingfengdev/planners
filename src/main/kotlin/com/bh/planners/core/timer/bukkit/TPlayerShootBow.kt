package com.bh.planners.core.timer.bukkit

import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.timer.AbstractTimer
import com.bh.planners.core.timer.Template
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityShootBowEvent
import taboolib.module.kether.ScriptContext

object TPlayerShootBow : AbstractTimer<EntityShootBowEvent>() {
    override val name: String
        get() = "player shoot"
    override val eventClazz: Class<EntityShootBowEvent>
        get() = EntityShootBowEvent::class.java

    override fun check(e: EntityShootBowEvent): Target? {
        val player = e.entity as? Player ?: return null
        return player.toTarget()
    }

    /**
     * arrow 箭
     * bow 弓
     * force 拉弓力度
     * shooter 拉弓者
     */
    override fun onStart(context: ScriptContext, template: Template, e: EntityShootBowEvent) {
        super.onStart(context, template, e)
        context.rootFrame().variables()["arrow"] = e.projectile
        context.rootFrame().variables()["bow"] = e.bow
        context.rootFrame().variables()["force"] = e.force
        context.rootFrame().variables()["shooter"] = e.entity.name
    }
}