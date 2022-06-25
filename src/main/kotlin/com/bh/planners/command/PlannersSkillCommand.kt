package com.bh.planners.command

import com.bh.planners.api.PlannersAPI
import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.addPoint
import com.bh.planners.api.hasJob
import com.bh.planners.core.kether.enhance.ActionSkillCast
import com.bh.planners.core.ui.Faceplate
import org.bukkit.Bukkit
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.function.adaptPlayer
import taboolib.common5.Coerce
import taboolib.platform.util.sendLang

@CommandHeader("plannersskill")
object PlannersSkillCommand {

    @CommandBody
    val tryCast = subCommand {
        dynamic("player") {
            suggestion<ProxyCommandSender> { sender, context -> Bukkit.getOnlinePlayers().map { it.name } }

            dynamic("value") {
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
                dynamic("level") {
                    execute<ProxyCommandSender> { sender, context, argument ->
                        val player = Bukkit.getPlayerExact(context.argument(-2))!!
                        val skill = PlannersAPI.getSkill(Coerce.toString(context.argument(-1)))
                            ?: error("Skill '${context.argument(-1)}' not found")

                        val level = Coerce.toInteger(argument)
                        ActionSkillCast.ContextImpl(adaptPlayer(player), skill, level).cast()
                    }
                }


            }
        }
    }


}