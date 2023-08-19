package com.bh.planners.endtime.timer

import com.bh.planners.api.common.Plugin
import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.timer.AbstractTimer
import com.bh.planners.core.timer.Template
import com.ipedg.minecraft.dragoncollect.event.PlayerCollectStartEvent
import taboolib.module.kether.ScriptContext

@Plugin("DragonCollect")
object DCollect : AbstractTimer<PlayerCollectStartEvent>() {

    override val name: String
        get() = "dragon collect"

    override val eventClazz: Class<PlayerCollectStartEvent>
        get() = PlayerCollectStartEvent::class.java

    override fun check(e: PlayerCollectStartEvent): Target {
        return e.player.toTarget()
    }

    override fun onStart(context: ScriptContext, template: Template, e: PlayerCollectStartEvent) {
        context["time"] = e.collectEntity.collectTime
        context["name"] = e.collectEntity.modelName
    }

}