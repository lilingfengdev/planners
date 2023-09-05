package com.bh.planners.core.effect.inline

import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.kether.rootVariables
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.Projectile
import org.bukkit.event.Event
import taboolib.library.kether.QuestContext
import taboolib.module.kether.ScriptContext

class IncidentHitBlock(val project: Projectile, val owner: Entity, val block: Block, val event: Event) : Incident {
    override fun inject(context: ScriptContext) {

        context.rootFrame().rootVariables()["@event"] = event
        context.rootFrame().rootVariables()["@owner"] = owner.target()
        context.rootFrame().rootVariables()["@loc"] = block.location.toTarget()
        context.rootFrame().rootVariables()["@location"] = block.location.toTarget()
        context.rootFrame().rootVariables()["@block"] = block
        context.rootFrame().rootVariables()["@projectile"] = project.target()
    }
}