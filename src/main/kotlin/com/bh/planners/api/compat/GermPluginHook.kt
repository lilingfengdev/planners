package com.bh.planners.api.compat

import com.bh.planners.api.PlannersAPI
import com.germ.germplugin.api.event.GermDeleteSrcEvent
import taboolib.common.platform.event.OptionalEvent
import taboolib.common.platform.event.SubscribeEvent

object GermPluginHook {

    @SubscribeEvent(bind = "com.germ.germplugin.api.event.GermDeleteSrcEvent")
    fun e(ope: OptionalEvent) {
        val e = ope.get<GermDeleteSrcEvent>()
        val name = e.name
        PlannersAPI.callKeyByGroup(e.player,name)
    }

}