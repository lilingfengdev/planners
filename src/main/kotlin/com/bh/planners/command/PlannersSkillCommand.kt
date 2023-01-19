package com.bh.planners.command

import com.bh.planners.api.ContextAPI
import com.bh.planners.api.PlannersAPI
import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.bind
import com.bh.planners.api.hasJob
import com.bh.planners.core.kether.game.ActionSkillCast
import com.bh.planners.core.ui.Faceplate
import org.bukkit.Bukkit
import org.bukkit.Material
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.function.adaptPlayer
import taboolib.common5.Coerce

@CommandHeader("plannersskill")
object PlannersSkillCommand {

    @CommandBody
    val tryCast = subCommand {
        dynamic("player") {
            suggestion<ProxyCommandSender> { sender, context -> Bukkit.getOnlinePlayers().map { it.name } }

            dynamic("value") {

                suggestion<ProxyCommandSender> { sender, context ->
                    val player = Bukkit.getPlayerExact(context.argument(-1))!!
                    if (player.hasJob) {
                        player.plannersProfile.getSkills().map { it.key }
                    } else emptyList()
                }
                execute<ProxyCommandSender> { sender, context, argument ->
                    val player = Bukkit.getPlayerExact(context.argument(-1))!!
                    if (player.hasJob) {
                        PlannersAPI.cast(player, argument)
                    }
                }
            }
        }
    }

    @CommandBody
    val upgrade = subCommand {
        dynamic("player") {
            suggestion<ProxyCommandSender> { sender, context -> Bukkit.getOnlinePlayers().map { it.name } }

            dynamic("value") {
                suggestion<ProxyCommandSender> { sender, context ->
                    val player = Bukkit.getPlayerExact(context.argument(-1))!!
                    if (player.hasJob) {
                        player.plannersProfile.getSkills().map { it.key }
                    } else emptyList()
                }
                execute<ProxyCommandSender> { sender, context, argument ->
                    val player = Bukkit.getPlayerExact(context.argument(-1))!!
                    if (player.hasJob) {
                        Faceplate(player, PlannersAPI.getSkill(argument) ?: return@execute).open()
                    }
                }
            }
        }
    }

    @CommandBody
    val directCast = subCommand {
        dynamic("player") {
            suggestion<ProxyCommandSender> { sender, context -> Bukkit.getOnlinePlayers().map { it.name } }
            dynamic("skill") {
                suggestion<ProxyCommandSender> { sender, context ->
                    PlannersAPI.skills.map { it.key }
                }
                dynamic("level") {
                    execute<ProxyCommandSender> { sender, context, argument ->
                        val player = Bukkit.getPlayerExact(context.argument(-2))!!
                        ContextAPI.create(player, context.argument(-1), Coerce.toInteger(argument))?.cast()
                    }
                }


            }
        }
    }

    @CommandBody
    val bind = subCommand {
        dynamic("player") {
            suggestion<ProxyCommandSender> { sender, context -> Bukkit.getOnlinePlayers().map { it.name } }
            dynamic("skill") {
                suggestion<ProxyCommandSender> { sender, context ->
                    PlannersAPI.skills.map { it.key }
                }
                dynamic("slot") {
                    suggestion<ProxyCommandSender> { sender, context ->
                        PlannersAPI.keySlots.map { it.key }
                    }
                    execute<ProxyCommandSender> { sender, context, argument ->
                        val player = Bukkit.getPlayerExact(context.argument(-2))!!
                        val skill = player.plannersProfile.getSkill(context.argument(-1))!!
                        player.plannersProfile.bind(skill, PlannersAPI.keySlots.firstOrNull { it.key == argument }!!)
                    }
                }


            }
        }
    }

    @CommandBody
    val clear = subCommand {
        dynamic("player") {

            suggestion<ProxyCommandSender> { sender, context ->
                listOf("*", *Bukkit.getOnlinePlayers().map { it.name }.toTypedArray())
            }

            dynamic("skill") {

                suggestion<ProxyCommandSender> { sender, context ->
                    listOf("*", *PlannersAPI.skills.map { it.key }.toTypedArray())
                }

                execute<ProxyCommandSender> { sender, context, argument ->
                    val player = Bukkit.getPlayerExact(context.argument(-1))!!
                    val profile = player.plannersProfile
                    if (argument == "*") {
                        profile.getSkills().forEach { PlannersAPI.resetSkillPoint(profile,it) }
                    } else {
                        val skill = profile.getSkill(argument) ?: return@execute
                        PlannersAPI.resetSkillPoint(profile,skill)
                    }
                }

            }
        }
    }

}