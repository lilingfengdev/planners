package com.bh.planners.core.timer.impl

import com.bh.planners.core.timer.AbstractTimer
import com.bh.planners.core.timer.Template
import org.bukkit.entity.Player
import org.bukkit.event.player.*
import taboolib.module.kether.ScriptContext

object TimerPlayerDropItem : AbstractTimer<PlayerDropItemEvent>() {
    override val name: String
        get() = "player bucked empty"
    override val eventClazz: Class<PlayerDropItemEvent>
        get() = PlayerDropItemEvent::class.java

    override fun check(e: PlayerDropItemEvent): Player? {
        return e.player
    }

    override fun onStart(context: ScriptContext, template: Template, e: PlayerDropItemEvent) {
        super.onStart(context, template, e)
        val itemStack = e.itemDrop.itemStack
        context["displayName"] = itemStack.itemMeta?.displayName
        context["lore"] = itemStack.itemMeta?.lore
        context["material"] = itemStack.type
        context["item"] = itemStack
    }

}
