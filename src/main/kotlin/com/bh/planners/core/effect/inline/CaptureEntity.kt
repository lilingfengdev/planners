package com.bh.planners.core.effect.inline

import com.bh.planners.core.kether.rootVariables
import com.bh.planners.core.pojo.Session
import com.bh.planners.core.effect.Target.Companion.toTarget
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import taboolib.module.kether.ScriptContext

class CaptureEntity(val entity: Entity) : InlineEvent {

    override val name: String
        get() = "onCapture"

    override fun inject(context: ScriptContext) {
        val session = context.rootFrame().rootVariables().get<Session>("@Context").orElse(null) ?: return
        val asPlayer = session.asPlayer
        context.rootFrame().rootVariables()["@Target"] = entity.toTarget()
        context.rootFrame().rootVariables()["@entity"] = entity
        context.rootFrame().rootVariables()["@entityId"] = entity.entityId
        context.rootFrame().rootVariables()["@entityUniqueId"] = entity.uniqueId
        context.rootFrame().rootVariables()["@isThis"] = entity == asPlayer
    }

}