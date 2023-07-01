package com.bh.planners.core.effect.inline

import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.kether.rootVariables
import org.bukkit.entity.Entity
import org.bukkit.entity.Projectile
import org.bukkit.event.Event
import taboolib.library.kether.QuestContext
import taboolib.module.kether.ScriptContext

class IncidentHitEntity(val owner: Entity, val entity: Entity, val event: Event, val project: Projectile, val vars: QuestContext.VarTable) : Incident {
    override fun inject(context: ScriptContext) {
        val entitys = Target.Container()
        val owners = Target.Container()
        val projects = Target.Container()

        val rootVariables = context.rootFrame().rootVariables()

        entitys += entity.toTarget()
        owners += owner.target()
        projects += project.toTarget()

        vars.toMap().map {
            rootVariables[it.key] = it.value
        }

        rootVariables["@Event"] = event
        rootVariables["entity"] = entitys
        rootVariables["owner"] = owners
        rootVariables["project"] = projects
    }
}