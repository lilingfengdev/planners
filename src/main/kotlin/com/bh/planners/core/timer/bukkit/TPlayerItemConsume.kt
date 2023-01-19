package com.bh.planners.core.timer.bukkit

import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.timer.AbstractTimer
import com.bh.planners.core.timer.Template
import org.bukkit.event.player.PlayerItemConsumeEvent
import taboolib.module.kether.ScriptContext

object TPlayerItemConsume : AbstractTimer<PlayerItemConsumeEvent>() {
    override val name: String
        get() = "player consume"
    override val eventClazz: Class<PlayerItemConsumeEvent>
        get() = PlayerItemConsumeEvent::class.java

    override fun check(e: PlayerItemConsumeEvent): Target? {
        return e.player.toTarget()
    }

    /**
     * *** 被消耗物品
     *      displayName 物品名
     *      lore 物品lore
     *      material 材质
     *      item 实例
     */
    override fun onStart(context: ScriptContext, template: Template, e: PlayerItemConsumeEvent) {
        super.onStart(context, template, e)
        val item = e.item
        context.rootFrame().variables()["displayName"] = item.itemMeta?.displayName
        context.rootFrame().variables()["lore"] = item.itemMeta?.lore
        context.rootFrame().variables()["material"] = item.type.toString()
        context.rootFrame().variables()["item"] = item
    }



}