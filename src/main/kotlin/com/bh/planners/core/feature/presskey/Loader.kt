package com.bh.planners.core.feature.presskey

import com.bh.planners.api.PlannersAPI
import com.bh.planners.api.PlannersOption
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.platform.BukkitPlugin
import java.nio.charset.StandardCharsets
import java.util.LinkedList

object Loader {

    const val CHANNEL_NAME = "planners:key_input"

    val packets = LinkedList<Packet>()

    @Awake(LifeCycle.ENABLE)
    fun e() {
        Bukkit.getMessenger()
            .registerIncomingPluginChannel(BukkitPlugin.getInstance(), CHANNEL_NAME, Handler())
    }


    class Handler : PluginMessageListener {
        override fun onPluginMessageReceived(channel: String, player: Player, bytes: ByteArray) {
            val packet =
                PlannersAPI.gson.fromJson(String(bytes, StandardCharsets.UTF_8).substring(1), Packet::class.java)
            packets += packet
            PressKeyEvents.Get(player, packet).call()
        }

    }

    class Packet {

        var key: Int = 0
        var action: Int = 0

        override fun toString(): String {
            return "Packet(key=$key, action=$action)"
        }

    }


}