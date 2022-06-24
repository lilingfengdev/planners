package com.bh.planners.command

import com.bh.planners.api.*
import com.bh.planners.api.PlannersAPI.hasJob
import com.bh.planners.api.PlannersAPI.plannersProfile
import org.bukkit.Bukkit
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.common5.Coerce
import taboolib.expansion.createHelper
import taboolib.platform.util.sendLang

@CommandHeader("plannerslevel")
object PlannersLevelCommand {


    @CommandBody
    val main = mainCommand {
        createHelper()
    }

    @CommandBody
    val give = subCommand {
        dynamic("player") {
            suggestion<ProxyCommandSender> { sender, context -> Bukkit.getOnlinePlayers().map { it.name } }

            dynamic("value") {
                execute<ProxyCommandSender> { sender, context, argument ->
                    val player = Bukkit.getPlayerExact(context.argument(-1))!!
                    if (player.hasJob) {
                        player.plannersProfile.addLevel(Coerce.toInteger(argument))
                        player.sendLang("player-get-level", player.plannersProfile.level)
                    }
                }
            }
        }
    }

    @CommandBody
    val take = subCommand {
        dynamic("player") {
            suggestion<ProxyCommandSender> { sender, context -> Bukkit.getOnlinePlayers().map { it.name } }

            dynamic("value") {
                execute<ProxyCommandSender> { sender, context, argument ->
                    val player = Bukkit.getPlayerExact(context.argument(-1))!!
                    if (player.hasJob) {
                        player.plannersProfile.addLevel(-Coerce.toInteger(argument))
                        player.sendLang("player-take-level", player.plannersProfile.level)
                    }
                }
            }
        }
    }


}