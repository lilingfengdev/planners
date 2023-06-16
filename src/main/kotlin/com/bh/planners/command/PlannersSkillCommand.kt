package com.bh.planners.command

import com.bh.planners.api.ContextAPI
import com.bh.planners.api.PlannersAPI
import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.bind
import com.bh.planners.api.hasJob
import org.bukkit.Bukkit
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.common5.Coerce
import taboolib.expansion.createHelper

@CommandHeader("plannersskill")
object PlannersSkillCommand {

    @CommandBody
    val main = mainCommand {
        createHelper()
    }

    @CommandBody
    val tryCast = subCommand {
        dynamic("player") {
            suggestion<ProxyCommandSender> { sender, context -> Bukkit.getOnlinePlayers().map { it.name } }

            dynamic("value") {

                suggestion<ProxyCommandSender> { sender, context ->
                    val player = Bukkit.getPlayerExact(context["player"])!!
                    if (player.hasJob) {
                        player.plannersProfile.getSkills().map { it.key }
                    } else emptyList()
                }
                execute<ProxyCommandSender> { sender, context, argument ->
                    val player = Bukkit.getPlayerExact(context["player"])!!
                    if (player.hasJob) {
                        PlannersAPI.cast(player, argument)
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
                        val player = Bukkit.getPlayerExact(context["player"])!!
                        ContextAPI.create(player, context["skill"], Coerce.toInteger(argument))?.cast()
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
                        val player = Bukkit.getPlayerExact(context["player"])!!
                        val skill = player.plannersProfile.getSkill(context["skill"])!!
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
                    val player = Bukkit.getPlayerExact(context["player"])!!
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

    @CommandBody
    val upgrade = PlannersSkillUpgradeCommand

}