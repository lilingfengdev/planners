package com.bh.planners.command

import com.bh.planners.api.PlannersAPI
import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.add
import com.bh.planners.api.hasJob
import com.bh.planners.api.set
import com.bh.planners.core.kether.bukkitPlayer
import com.bh.planners.core.ui.Faceplate
import org.bukkit.Bukkit
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.player
import taboolib.common.platform.command.subCommand

@CommandHeader("plannersskillup")
object PlannersSkillUpgradeCommand {

    @CommandBody
    val ui = subCommand {

        dynamic("player") {
            suggestion<ProxyCommandSender> { sender, context -> Bukkit.getOnlinePlayers().map { it.name } }

            dynamic("skill") {
                suggestion<ProxyCommandSender> { sender, context ->
                    val player = context.player("player").bukkitPlayer()!!
                    if (player.hasJob) {
                        player.plannersProfile.getSkills().map { it.key }
                    } else emptyList()
                }

                execute<ProxyCommandSender> { sender, context, argument ->
                    val player = context.player("player").bukkitPlayer()!!
                    if (player.hasJob) {
                        Faceplate(player, PlannersAPI.getSkill(argument) ?: return@execute).open()
                    }
                }
            }
        }
    }

    @CommandBody
    val set = subCommand {

        dynamic("player") {
            suggestion<ProxyCommandSender> { sender, context -> Bukkit.getOnlinePlayers().map { it.name } }

            dynamic("skill") {
                suggestion<ProxyCommandSender> { sender, context ->
                    val player = context.player("player").bukkitPlayer()!!
                    if (player.hasJob) {
                        player.plannersProfile.getSkills().map { it.key }
                    } else emptyList()
                }

                dynamic("number") {
                    execute<ProxyCommandSender> { sender, context, argument ->
                        val player = context.player("player").bukkitPlayer()!!
                        if (player.hasJob) {
                            val profile = player.plannersProfile
                            val skill = profile.getSkill(context["skill"]) ?: return@execute
                            profile.set(skill, argument.toInt())
                        }
                    }
                }
            }
        }
    }

    @CommandBody
    val add = subCommand {

        dynamic("player") {
            suggestion<ProxyCommandSender> { sender, context -> Bukkit.getOnlinePlayers().map { it.name } }

            dynamic("skill") {
                suggestion<ProxyCommandSender> { sender, context ->
                    val player = context.player("player").bukkitPlayer()!!
                    if (player.hasJob) {
                        player.plannersProfile.getSkills().map { it.key }
                    } else emptyList()
                }

                dynamic("number") {
                    execute<ProxyCommandSender> { sender, context, argument ->
                        val player = context.player("player").bukkitPlayer()!!
                        if (player.hasJob) {
                            val profile = player.plannersProfile
                            val skill = profile.getSkill(context["skill"]) ?: return@execute
                            profile.add(skill, argument.toInt())
                        }
                    }
                }
            }
        }
    }
}