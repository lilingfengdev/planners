package com.bh.planners.core.ui

import com.bh.planners.api.PlannersAPI.plannersProfile
import org.bukkit.entity.Player

abstract class IUI(val viewer: Player) {

    val profile by lazy { viewer.plannersProfile }

    abstract fun open()

    companion object {

        fun Player.open(ui: IUI) {
            ui.open()
        }

    }


}