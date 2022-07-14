package com.bh.planners.core.kether.game

import com.bh.planners.api.PlannersAPI
import com.google.gson.JsonObject

class ActionToast {


    class Builder {

        var material: String? = null
        var data: String? = null
        var title: String? = null
        var description: String? = "Planners toast message."
        var background: String? = "minecraft:textures/gui/advancements/backgrounds/adventure.png"
        var frame: String? = null
        var announceToChat = false
        var showToast = true
        var hidden = true
        var trigger = "minecraft:impossible"
        var criteria = JsonObject()

        fun createJSON(): String = PlannersAPI.gson.toJson(create())

        fun create(): JsonObject {

            val display = JsonObject()

            val icon = JsonObject()

            icon.addProperty("item", material)
            if (this.data != null) {
                icon.addProperty("nbt", data)
            }

            display.add("icon", icon)
            display.addProperty("title", this.title)
            display.addProperty("description", this.description)
            display.addProperty("background", this.background)
            display.addProperty("frame", frame)
            display.addProperty("announce_to_chat", this.announceToChat)
            display.addProperty("show_toast", this.showToast)
            display.addProperty("hidden", this.hidden)

            criteria.add("impossible", JsonObject().also {
                it.addProperty("trigger", trigger)
            })

            return JsonObject().also {
                it.add("criteria", criteria)
                it.add("display", display)
            }
        }

    }


}