package com.bh.planners.core.kether.event

import com.bh.planners.api.event.proxy.AbstractProxyDamageEvent.Companion.damageMemory
import com.bh.planners.api.event.proxy.ProxyDamageEvent
import com.bh.planners.core.kether.ActionEvent.Companion.event
import com.bh.planners.core.kether.eventParser
import com.bh.planners.core.kether.tryGet
import taboolib.common.platform.function.info
import taboolib.module.kether.KetherParser
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import java.util.concurrent.CompletableFuture

class ActionEventCrit : ScriptAction<Void>() {
    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val event = frame.event()
        if (event is ProxyDamageEvent) {
            event.damageMemory()?.setLabel("@Crit", true)
            info("暴击了")
        }
        return CompletableFuture.completedFuture(null)
    }

    companion object {

        @KetherParser(["crit"])
        fun parser() = eventParser {
            ActionEventCrit()
        }

    }

}