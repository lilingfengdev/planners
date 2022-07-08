package com.bh.planners.command

import com.bh.planners.Planners
import com.bh.planners.api.PlannersOption
import com.bh.planners.api.event.PluginReloadEvent
import com.bh.planners.core.kether.namespaces
import com.bh.planners.core.skill.effect.EffectOption
import com.bh.planners.core.skill.effect.EffectSpawner
import com.bh.planners.core.skill.effect.EffectSpawner.Companion.sendParticle
import com.bh.planners.core.skill.effect.common.Sphere
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Particle
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.ProxyParticle
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.util.Vector
import taboolib.expansion.createHelper
import taboolib.module.chat.colored
import taboolib.module.kether.KetherFunction
import taboolib.module.kether.runKether

@CommandHeader("planners", aliases = ["ps", "pl"], permission = "planners.command")
object PlannersCommand {

    @CommandBody
    val main = mainCommand {
        createHelper()
    }

    @CommandBody
    val reload = subCommand {

        execute<ProxyCommandSender> { sender, context, argument ->
            Planners.config.reload()
            PluginReloadEvent().call()
            sender.sendMessage("reload successful.")
        }

    }

    @CommandBody
    val job = PlannersJobCommand

    @CommandBody
    val point = PlannersPointCommand

    @CommandBody
    val level = PlannersLevelCommand

    @CommandBody
    val exp = PlannersExperienceCommand

    @CommandBody
    val transfer = PlannersTransferCommand

    @CommandBody
    val mana = PlannersManaCommand

    @CommandBody
    val skill = PlannersSkillCommand

    @CommandBody
    val info = subCommand {
        dynamic("player") {
            suggestion<ProxyCommandSender> { sender, context -> Bukkit.getOnlinePlayers().map { it.name } }

            execute<ProxyCommandSender> { sender, context, argument ->
                val playerExact = Bukkit.getPlayerExact(argument) ?: return@execute
                runKether {
                    KetherFunction
                        .parse(PlannersOption.infos, namespace = namespaces, sender = adaptPlayer(playerExact))
                        .forEach { sender.sendMessage(it.colored()) }
                }
            }
        }
    }

    @CommandBody
    val test = subCommand {
        execute<Player> { sender, context, argument ->
            val spawner = EffectSpawner(EffectOption("END_ROD"))
            val vector = sender.location.direction

        }
    }

}
