package com.bh.planners.core.timer.bukkit

import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.timer.AbstractTimer
import com.bh.planners.core.timer.Template
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByBlockEvent
import taboolib.module.kether.ScriptContext

object TPlayerBlockDamaged : AbstractTimer<EntityDamageByBlockEvent>() {
    override val name: String
        get() = "player damaged block"
    override val eventClazz: Class<EntityDamageByBlockEvent>
        get() = EntityDamageByBlockEvent::class.java

    override fun check(e: EntityDamageByBlockEvent): Target? {
        return (e.entity as? Player)?.toTarget()
    }

    /**
     * material 伤害来源方块材质
     * block | damager 方块实例
     * damage 伤害数值
     */
    override fun onStart(context: ScriptContext, template: Template, e: EntityDamageByBlockEvent) {
        super.onStart(context, template, e)
        context.rootFrame().variables()["block"] = e.damager
        context.rootFrame().variables()["damager"] = e.damager
        context.rootFrame().variables()["material"] = e.damager?.blockData?.material?.toString()
        context.rootFrame().variables()["damage"] = e.damage
    }
}