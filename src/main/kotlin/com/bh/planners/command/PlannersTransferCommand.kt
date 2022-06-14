package com.bh.planners.command

import com.bh.planners.api.PlannersAPI
import com.bh.planners.api.PlannersAPI.hasJob
import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.addPoint
import com.bh.planners.api.setPoint
import com.bh.planners.api.transfer
import com.bh.planners.core.ui.TransferJobUI
import org.bukkit.Bukkit
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.subCommand
import taboolib.common5.Coerce
import taboolib.module.lang.sendLang
import taboolib.platform.util.sendLang

@CommandHeader("transfer")
object PlannersTransferCommand {

    @CommandBody
    val open = subCommand {
        dynamic("player") {
            suggestion<ProxyCommandSender> { sender, context -> Bukkit.getOnlinePlayers().map { it.name } }

            execute<ProxyCommandSender> { sender, context, argument ->
                val player = Bukkit.getPlayerExact(argument)!!
                if (player.hasJob) {
                    TransferJobUI(player).open()
                }
            }
        }
    }

    @CommandBody
    val to = subCommand {

        dynamic("player") {
            suggestion<ProxyCommandSender> { sender, context -> Bukkit.getOnlinePlayers().map { it.name } }

            dynamic("value") {
                execute<ProxyCommandSender> { sender, context, argument ->
                    val player = Bukkit.getPlayerExact(context.argument(-1))!!
                    if (player.hasJob) {
                        val job = PlannersAPI.getJob(argument)
                        if (player.plannersProfile.transfer(job)) {
                            sender.sendLang("player-transfer-successful", job.option.name)
                        } else {
                            sender.sendLang("player-transfer-failed")
                        }
                    }
                }
            }
        }

    }


}