package com.bh.planners.command

import com.bh.planners.api.PlannersAPI
import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.attemptAcceptJob
import com.bh.planners.api.hasJob
import com.bh.planners.api.reset
import com.bh.planners.core.kether.bukkitPlayer
import com.bh.planners.core.ui.Backpack
import com.bh.planners.core.ui.JobUI
import org.bukkit.Bukkit
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.*
import taboolib.expansion.createHelper
import taboolib.module.lang.sendLang
import taboolib.platform.util.sendLang

@CommandHeader("plannersjob")
object PlannersJobCommand {

    @CommandBody
    val main = mainCommand {
        createHelper()
    }


    @CommandBody
    val select = subCommand {
        dynamic("player") {
            suggestion<ProxyCommandSender> { _, _ -> Bukkit.getOnlinePlayers().map { it.name } }

            execute<ProxyCommandSender> { _, _, argument ->
                val player = Bukkit.getPlayerExact(argument)!!

                if (player.plannersProfile.job != null) {
                    player.sendLang("job-exists", player.plannersProfile.job!!.instance.option.name)
                    return@execute
                }

                JobUI(player).open()

            }

        }
    }

    @CommandBody
    val call = subCommand {
        dynamic("player") {
            suggestion<ProxyCommandSender> { _, _ -> Bukkit.getOnlinePlayers().map { it.name } }

            dynamic("key slot") {
                execute<ProxyCommandSender> { _, context, argument ->
                    val player = context.player("player").bukkitPlayer()!!
                    PlannersAPI.callKeyById(player, argument)
                }
            }

        }
    }

    @CommandBody
    val cast = subCommand {
        dynamic("player") {
            suggestion<ProxyCommandSender> { _, _ -> Bukkit.getOnlinePlayers().map { it.name } }

            dynamic("skill") {
                suggestion<ProxyCommandSender> { _, context ->
                    Bukkit.getPlayerExact(context.argument(-1))!!.plannersProfile.getSkills().map { it.key }
                }

                execute<ProxyCommandSender> { _, context, argument ->
                    val playerExact = context.player("player").bukkitPlayer() ?: return@execute
                    val skill = PlannersAPI.skills.firstOrNull { it.key == argument } ?: return@execute
                    PlannersAPI.cast(playerExact, skill).handler(playerExact, skill)
                }

            }

        }
    }

    @CommandBody
    val backpack = subCommand {
        dynamic("player") {
            suggestion<ProxyCommandSender> { _, _ -> Bukkit.getOnlinePlayers().map { it.name } }

            execute<ProxyCommandSender> { _, _, argument ->
                val playerExact = Bukkit.getPlayerExact(argument) ?: return@execute

                if (playerExact.hasJob) {
                    Backpack(playerExact).open()
                }

            }

        }
    }

    @CommandBody
    val set = subCommand {
        dynamic("player") {
            suggestion<ProxyCommandSender> { _, _ -> Bukkit.getOnlinePlayers().map { it.name } }

            dynamic("job") {
                suggestion<ProxyCommandSender> { _, _ ->
                    PlannersAPI.routers.map { it.key }
                }

                execute<ProxyCommandSender> { sender, context, argument ->
                    val playerExact = context.player("player").bukkitPlayer()!!
                    if (playerExact.hasJob) {
                        sender.sendLang("console-player-job-exists", playerExact.plannersProfile.job!!.name)
                        return@execute
                    }
                    val router = PlannersAPI.routers.first { it.key == argument }
                    val job = PlannersAPI.getRouterStartJob(router)
                    if (playerExact.plannersProfile.attemptAcceptJob(job)) {
                        playerExact.sendLang("player-job-selected", job.option.name)
                    }
                }

            }

        }
    }

    @CommandBody
    val clear = subCommand {
        dynamic("player") {
            suggestion<ProxyCommandSender> { _, _ -> Bukkit.getOnlinePlayers().map { it.name } }

            execute<ProxyCommandSender> { sender, _, argument ->
                val playerExact = Bukkit.getPlayerExact(argument)!!
                if (!playerExact.hasJob) {
                    sender.sendLang("console-player-job-not-exists")
                    return@execute
                }
                playerExact.plannersProfile.reset()
                playerExact.sendLang("player-job-clear")
            }

        }
    }


}
