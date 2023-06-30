package com.bh.planners.core.effect.inline

import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.kether.rootVariables
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.event.Event
import taboolib.module.kether.ScriptContext

class IncidentHitBlock(val owner: Entity, val block: Block, val event: Event) : Incident {
    override fun inject(context: ScriptContext) {
        val loc = Target.Container().add(block.location.toTarget())
        val owner = Target.Container().add(owner.target())

        context.rootFrame().rootVariables()["@Event"] = event
        context.rootFrame().rootVariables()["owner"] = owner
        context.rootFrame().rootVariables()["loc"] = loc
        context.rootFrame().rootVariables()["location"] = loc
        context.rootFrame().rootVariables()["block"] = block
    }
}