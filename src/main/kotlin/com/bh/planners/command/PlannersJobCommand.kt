package com.bh.planners.command

import com.bh.planners.api.PlannersAPI
import com.bh.planners.api.PlannersAPI.profile
import com.bh.planners.api.event.PlayerKeydownEvent
import com.bh.planners.core.ui.JobUI
import org.bukkit.Bukkit
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.expansion.createHelper
import taboolib.platform.util.sendLang

object PlannersJobCommand {

    @CommandBody
    val main = mainCommand {
        createHelper()
    }


    @CommandBody
    val selectui = subCommand {
        dynamic("player") {
            suggestion<ProxyCommandSender> { sender, context -> Bukkit.getOnlinePlayers().map { it.name } }

            execute<ProxyCommandSender> { sender, context, argument ->
                val player = Bukkit.getPlayerExact(argument)!!

                if (player.profile().job != null) {
                    player.sendLang("job-exists", player.profile().job!!.instance.option.name)
                    return@execute
                }

                JobUI(player).open()

            }

        }
    }

    @CommandBody
    val callkey = subCommand {
        dynamic("player") {
            suggestion<ProxyCommandSender> { sender, context -> Bukkit.getOnlinePlayers().map { it.name } }

            dynamic("key slot") {
                execute<ProxyCommandSender> { sender, context, argument ->
                    val player = Bukkit.getPlayerExact(context.argument(-1))!!
                    val keySlot = PlannersAPI.keySlots.firstOrNull { it.key == argument }
                        ?: error("KeySlot '$argument' not found.")
                    PlayerKeydownEvent(player, keySlot).call()
                }
            }

        }
    }


}
