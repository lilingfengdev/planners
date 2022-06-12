package com.bh.planners.command

import com.bh.planners.api.PlannersAPI
import com.bh.planners.api.PlannersAPI.hasJob
import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.addPoint
import com.bh.planners.api.event.PlayerKeydownEvent
import com.bh.planners.api.setPoint
import com.bh.planners.core.ui.IUI.Companion.open
import com.bh.planners.core.ui.JobUI
import com.bh.planners.core.ui.SkillBackpack
import com.bh.planners.core.ui.SkillIcon
import com.bh.planners.core.ui.SkillUI
import org.bukkit.Bukkit
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.common5.Coerce
import taboolib.expansion.createHelper
import taboolib.platform.util.sendLang

@CommandHeader("plannerspoint")
object PlannersPointCommand {

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
                        player.sendLang("player-get-point", argument)
                        player.plannersProfile.addPoint(Coerce.toInteger(argument))
                    }
                }
            }
            execute<ProxyCommandSender> { sender, context, argument ->
                val player = Bukkit.getPlayerExact(argument)!!
                if (player.hasJob) {
                    player.sendLang("player-get-point", 1)
                    player.plannersProfile.addPoint(1)
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
                        player.sendLang("player-take-point", argument)
                        player.plannersProfile.addPoint(-Coerce.toInteger(argument))
                    }
                }
            }
            execute<ProxyCommandSender> { sender, context, argument ->
                val player = Bukkit.getPlayerExact(argument)!!
                if (player.hasJob) {
                    player.sendLang("player-take-point", 1)
                    player.plannersProfile.addPoint(-1)
                }
            }
        }
    }

    @CommandBody
    val set = subCommand {
        dynamic("player") {
            suggestion<ProxyCommandSender> { sender, context -> Bukkit.getOnlinePlayers().map { it.name } }

            dynamic("value") {
                execute<ProxyCommandSender> { sender, context, argument ->
                    val player = Bukkit.getPlayerExact(context.argument(-1))!!
                    if (player.hasJob) {
                        player.sendLang("player-set-point", argument)
                        player.plannersProfile.addPoint(-Coerce.toInteger(argument))
                    }
                }
            }
            execute<ProxyCommandSender> { sender, context, argument ->
                val player = Bukkit.getPlayerExact(argument)!!
                if (player.hasJob) {
                    player.sendLang("player-clear-point", 0)
                    player.plannersProfile.setPoint(0)
                }
            }
        }
    }

    @CommandBody
    val clear = subCommand {
        dynamic("player") {
            suggestion<ProxyCommandSender> { sender, context -> Bukkit.getOnlinePlayers().map { it.name } }

            execute<ProxyCommandSender> { sender, context, argument ->
                val player = Bukkit.getPlayerExact(argument)!!
                if (player.hasJob) {
                    player.sendLang("player-clear-point", 0)
                    player.plannersProfile.setPoint(0)
                }
            }
        }
    }
}
