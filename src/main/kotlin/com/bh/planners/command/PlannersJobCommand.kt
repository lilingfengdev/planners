package com.bh.planners.command

import com.bh.planners.api.PlannersAPI
import com.bh.planners.api.PlannersAPI.hasJob
import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.attemptAcceptJob
import com.bh.planners.api.event.PlayerKeydownEvent
import com.bh.planners.api.reset
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
            suggestion<ProxyCommandSender> { sender, context -> Bukkit.getOnlinePlayers().map { it.name } }

            execute<ProxyCommandSender> { sender, context, argument ->
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
            suggestion<ProxyCommandSender> { sender, context -> Bukkit.getOnlinePlayers().map { it.name } }

            dynamic("key slot") {
                execute<ProxyCommandSender> { sender, context, argument ->
                    val player = Bukkit.getPlayerExact(context.argument(-1))!!
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
                    val playerExact = Bukkit.getPlayerExact(context.argument(-1)) ?: return@execute
                    val skill = PlannersAPI.skills.firstOrNull { it.key == argument } ?: return@execute
                    PlannersAPI.cast(playerExact, skill).handler(playerExact, skill)
                }

            }

        }
    }

    @CommandBody
    val skill = subCommand {
        dynamic("player") {
            suggestion<ProxyCommandSender> { _, _ -> Bukkit.getOnlinePlayers().map { it.name } }

            execute<ProxyCommandSender> { _, context, argument ->
                val playerExact = Bukkit.getPlayerExact(argument) ?: return@execute

                if (playerExact.hasJob) {
                    SkillBackpack(playerExact).open()
                }

            }

        }
    }

    @CommandBody
    val set = subCommand {
        dynamic("player") {
            suggestion<ProxyCommandSender> { _, _ -> Bukkit.getOnlinePlayers().map { it.name } }

            dynamic("job") {
                suggestion<ProxyCommandSender> { _, context ->
                    PlannersAPI.routers.map { it.key }
                }

                execute<ProxyCommandSender> { sender, context, argument ->
                    val playerExact = Bukkit.getPlayerExact(context.argument(-1))!!
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

            execute<ProxyCommandSender> { sender, context, argument ->
                val playerExact = Bukkit.getPlayerExact(context.argument(-1))!!
                if (!playerExact.hasJob) {
                    sender.sendLang("console-player-job-not-exists", playerExact.plannersProfile.job!!.name)
                    return@execute
                }
                playerExact.plannersProfile.reset()
                playerExact.sendLang("player-job-clear")
            }

        }
    }


}
