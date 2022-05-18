package com.bh.planners.command

import com.bh.planners.Planners
import com.bh.planners.api.event.PluginReloadEvent
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.expansion.createHelper

@CommandHeader("planners", permission = "planners.command")
class PlannersCommand {

    val main = mainCommand {
        createHelper()
    }

    val reload = subCommand {

        execute<ProxyCommandSender> { sender, context, argument ->
            Planners.config.reload()
            PluginReloadEvent().call()
            sender.sendMessage("reload successful.")
        }

    }

}
