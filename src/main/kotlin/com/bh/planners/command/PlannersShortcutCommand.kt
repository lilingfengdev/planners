package com.bh.planners.command

import com.bh.planners.api.PlannersAPI
import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.PlannersAPI.plannersProfileIsLoaded
import com.bh.planners.api.clearShortcut
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.*

object PlannersShortcutCommand {

    @CommandBody
    val clear = subCommand {
        dynamic("player") {
            suggestPlayers()
            dynamic("id") {
                subCommand { PlannersAPI.keySlots.map { it.key } }
                execute<ProxyCommandSender> { sender, context, argument ->
                    val player = context.player("player").castSafely<Player>() ?: return@execute
                    if (player.plannersProfileIsLoaded) {
                        if (argument == "*") {
                            PlannersAPI.keySlots.forEach { player.plannersProfile.clearShortcut(it) }
                        } else {
                            player.plannersProfile.clearShortcut(argument)
                        }
                    }
                }

            }
        }
    }


}