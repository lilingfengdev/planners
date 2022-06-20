package com.bh.planners.core.skill.effect.inline

import com.bh.planners.core.kether.rootVariables
import com.bh.planners.core.pojo.Session
import org.bukkit.entity.LivingEntity
import taboolib.module.kether.ScriptContext

class Capture(val entity: LivingEntity) : InlineEvent {

    override val name: String
        get() = "onCapture"

    override fun inject(context: ScriptContext) {
        val session = context.rootFrame().rootVariables().get<Session>("@Session").orElse(null) ?: return
        val asPlayer = session.asPlayer
        context.rootFrame().rootVariables()["@entity"] = entity
        context.rootFrame().rootVariables()["@entityId"] = entity.entityId
        context.rootFrame().rootVariables()["@entityUniqueId"] = entity.uniqueId
        context.rootFrame().rootVariables()["@isThis"] = entity == asPlayer
    }

}