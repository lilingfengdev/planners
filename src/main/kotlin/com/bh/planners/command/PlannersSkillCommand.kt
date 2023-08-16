package com.bh.planners.command

import com.bh.planners.api.ContextAPI
import com.bh.planners.api.PlannersAPI
import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.bind
import com.bh.planners.api.hasJob
import org.bukkit.Bukkit
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.*
import taboolib.common5.Coerce
import taboolib.expansion.createHelper
import taboolib.module.chat.ComponentText
import taboolib.module.chat.colored

@CommandHeader("plannersskill")
object PlannersSkillCommand {

    @CommandBody
    val main = mainCommand {
        createHelper()
    }

    @CommandBody
    val tryCast = subCommand {
        player {
            dynamic("value") {

                suggestion<ProxyCommandSender> { _, context ->
                    val player = Bukkit.getPlayerExact(context["player"])!!
                    if (player.hasJob) {
                        player.plannersProfile.getSkills().map { it.key }
                    } else emptyList()
                }
                execute<ProxyCommandSender> { _, context, argument ->
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
        player {
            dynamic("skill") {
                suggestion<ProxyCommandSender> { _, _ ->
                    PlannersAPI.skills.map { it.key }
                }
                dynamic("level") {
                    execute<ProxyCommandSender> { _, context, argument ->
                        val player = Bukkit.getPlayerExact(context["player"])!!
                        ContextAPI.create(player, context["skill"], Coerce.toInteger(argument))?.cast()
                    }
                }
            }
        }
    }

    @CommandBody
    val bind = subCommand {
        player {
            dynamic("skill") {
                suggestion<ProxyCommandSender> { _, _ ->
                    PlannersAPI.skills.map { it.key }
                }
                dynamic("slot") {
                    suggestion<ProxyCommandSender> { _, _ ->
                        PlannersAPI.keySlots.map { it.key }
                    }
                    execute<ProxyCommandSender> { _, context, argument ->
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
        player {

            dynamic("skill") {

                suggestion<ProxyCommandSender> { _, _ ->
                    listOf("*", *PlannersAPI.skills.map { it.key }.toTypedArray())
                }

                execute<ProxyCommandSender> { _, context, argument ->
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
    val list = subCommand {
        execute<ProxyCommandSender> { sender, _, _ ->
            PlannersAPI.skills.forEachIndexed { index, skill ->
                ComponentText.empty()
                    .append("&d${index+1}&7.&b${skill.option.name}".colored())
                    .hoverText("&e左键释放此技能&c(1级)".colored())
                    .clickRunCommand("/pl skill directCast ${sender.name} ${skill.key} 1")
                    .sendTo(sender)
            }
        }
    }

    @CommandBody
    val upgrade = PlannersSkillUpgradeCommand

}