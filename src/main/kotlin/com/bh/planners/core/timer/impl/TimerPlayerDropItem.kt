package com.bh.planners.core.timer.impl

import com.bh.planners.core.timer.AbstractTimer
import com.bh.planners.core.timer.Template
import org.bukkit.event.player.*
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.kether.ScriptContext

object TimerPlayerDropItem : AbstractTimer<PlayerDropItemEvent>() {
    override val name: String
        get() = "player bucked empty"
    override val eventClazz: Class<PlayerDropItemEvent>
        get() = PlayerDropItemEvent::class.java

    override fun check(e: PlayerDropItemEvent): ProxyCommandSender? {
        return adaptPlayer(e.player)
    }

    override fun onStart(context: ScriptContext, template: Template, e: PlayerDropItemEvent) {
        super.onStart(context, template, e)
        val itemStack = e.itemDrop.itemStack
        context.rootFrame().variables()["displayName"] = itemStack.itemMeta?.displayName
        context.rootFrame().variables()["lore"] = itemStack.itemMeta?.lore
        context.rootFrame().variables()["material"] = itemStack.type
        context.rootFrame().variables()["item"] = itemStack
    }

}
