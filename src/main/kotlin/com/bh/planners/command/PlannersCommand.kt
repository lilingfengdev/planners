package com.bh.planners.command

import com.bh.planners.Planners
import com.bh.planners.api.event.PluginReloadEvent
import org.bukkit.Color
import org.bukkit.Particle
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.expansion.createHelper

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
    val combat = PlannersCombatCommand

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
    val test = subCommand {
        execute<Player> { sender, context, argument ->
            val location = sender.location
            val color = java.awt.Color(187, 255, 255)
            location.world!!.spawnParticle(
                Particle.REDSTONE,
                location.getX(),
                location.getY(),
                location.getZ(),
                0,
                (color.red / 255.0f).toDouble(),
                (color.green / 255.0f).toDouble(),
                (color.blue / 255.0f).toDouble(),
                1.0
            )

        }
    }
}
