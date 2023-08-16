package com.bh.planners.core.kether.game

import com.bh.planners.api.PlannersAPI
import com.bh.planners.core.kether.*
import com.bh.planners.core.kether.common.KetherHelper.containerOrSender
import com.bh.planners.core.kether.common.KetherHelper.materialOrStone
import com.bh.planners.core.kether.common.SimpleKetherParser
import com.google.gson.JsonObject
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import taboolib.common.platform.function.submit
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import taboolib.platform.BukkitPlugin
import java.util.*
import java.util.concurrent.CompletableFuture

object ActionToast : SimpleKetherParser("toast") {

    override fun run(): ScriptActionParser<out Any?> {
        return combinationParser {
            it.group(
                    materialOrStone(),
                    text(),
                    command("data", "nbt", then = text()).option().defaultsTo("{}"),
                    command("frame", then = text()).option().defaultsTo("challenge"),
                    containerOrSender()
            ).apply(it) { material, text, data, frame, container ->
                now {
                    container.forEachPlayer {
                        execute(this, material.name, text, data, frame)
                    }
                }
            }
        }
    }

    class Builder {

        var material: String? = "STONE"
        var data: String? = null
        var title: String? = null
        var description: String? = "Planners toast message."
        var background: String? = "minecraft:textures/gui/advancements/backgrounds/adventure.png"
        var frame: String? = "challenge"
        var announceToChat = false
        var showToast = true
        var hidden = true
        var trigger = "minecraft:impossible"
        var criteria = JsonObject()

        fun createJSON(): String = PlannersAPI.gson.toJson(create())

        fun create(): JsonObject {
            val json = JsonObject()
            val icon = JsonObject()
            val display = JsonObject()
            val trigger = JsonObject()
            icon.addProperty("item", this.material)
            if (this.data != null) {
                icon.addProperty("nbt", this.data)
            }
            display.add("icon", icon)
            display.addProperty("title", this.title)
            display.addProperty("description", this.description)
            display.addProperty("background", this.background)
            display.addProperty("frame", frame)
            display.addProperty("announce_to_chat", this.announceToChat)
            display.addProperty("show_toast", this.showToast)
            display.addProperty("hidden", this.hidden)
            trigger.addProperty("trigger", this.trigger)
            criteria.add("impossible", trigger)
            json.add("criteria", criteria)
            json.add("display", display)
            return json
        }
    }

    fun create(material: String, message: String, data: String, frame: String): Builder {
        val builder = Builder()
        builder.material = "minecraft:$material"
        builder.title = message
        builder.data = data
        builder.frame = frame
        return builder
    }

    fun execute(player: Player, material: String, message: String, data: String, frame: String) {
        val id = NamespacedKey(BukkitPlugin.getInstance(), "temp-" + UUID.randomUUID().toString())
        Bukkit.getUnsafe().loadAdvancement(id, create(material, message, data, frame).createJSON())
        val advancement = Bukkit.getAdvancement(id)
        val progress = player.getAdvancementProgress(advancement!!)

        if (!progress.isDone) {
            progress.remainingCriteria.forEach { progress.awardCriteria(it) }
        }
        submit(delay = 20) {
            if (progress.isDone) {
                progress.awardedCriteria.forEach { progress.revokeCriteria(it) }
            }
            Bukkit.getUnsafe().removeAdvancement(id)
        }
    }

}