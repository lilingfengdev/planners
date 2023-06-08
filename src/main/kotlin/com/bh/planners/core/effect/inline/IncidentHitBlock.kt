package com.bh.planners.core.effect.inline

import com.bh.planners.core.kether.rootVariables
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.event.Event
import taboolib.module.kether.ScriptContext

class IncidentHitBlock(val owner: Entity, val block: Block, val event: Event) : Incident {
    override fun inject(context: ScriptContext) {
        context.rootFrame().rootVariables()["@Event"] = event
        context.rootFrame().rootVariables()["owner"] = owner
        context.rootFrame().rootVariables()["loc"] = block.location
        context.rootFrame().rootVariables()["location"] = block.location
        context.rootFrame().rootVariables()["block"] = block
    }
}