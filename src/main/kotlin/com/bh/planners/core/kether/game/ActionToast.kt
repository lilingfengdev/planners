package com.bh.planners.core.kether.game

import com.bh.planners.api.PlannersAPI
import com.bh.planners.core.kether.*
import com.google.gson.JsonObject
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import taboolib.common.platform.function.submit
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.KetherParser
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.scriptParser
import taboolib.platform.BukkitPlugin
import java.util.*
import java.util.concurrent.CompletableFuture

class ActionToast(
    val material: ParsedAction<*>,
    val message: ParsedAction<*>,
    val data: ParsedAction<*>,
    val frame: ParsedAction<*>,
    val selector: ParsedAction<*>?
) : ScriptAction<Void>() {


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


    companion object {

        /**
         * toast material title <data: action> <frame: action(challenge)>
         */
        @KetherParser(["toast"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            val material = it.next(ArgTypes.ACTION)
            val message = it.next(ArgTypes.ACTION)
            val data = it.tryGet(arrayOf("data", "nbt"), "{}")!!
            val frame = it.tryGet(arrayOf("frame"), "challenge")!!
            ActionToast(material, message, data, frame, it.selectorAction())
        }

    }

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        frame.runTransfer0<String>(material) { material ->
            frame.runTransfer0<String>(message) { message ->
                frame.runTransfer0<String>(data) { data ->
                    frame.runTransfer0<String>(this.frame) { f ->
                        if (selector != null) {
                            frame.createContainer(selector).thenAccept {
                                submit {
                                    it.forEachPlayer { execute(this, material, message, data, f) }
                                }
                            }
                        } else {
                            submit { execute(frame.asPlayer() ?: return@submit, material, message, data, f) }
                        }
                    }
                }
            }
        }

        return CompletableFuture.completedFuture(null)
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