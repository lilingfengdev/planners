package com.bh.planners.command

import com.bh.planners.Planners
import com.bh.planners.api.event.PluginReloadEvent
import com.bh.planners.core.ui.JobUI
import org.bukkit.Bukkit
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.expansion.createHelper

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
                JobUI(player).open()
            }

        }
    }


}
