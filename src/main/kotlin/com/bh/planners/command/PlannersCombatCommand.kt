package com.bh.planners.command

import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.combat.Combat
import com.bh.planners.api.combat.Combat.disableCombat
import com.bh.planners.api.combat.Combat.enableCombat
import com.bh.planners.api.combat.Combat.isCombat
import com.bh.planners.api.combat.Combat.isCombatLocal
import com.bh.planners.api.combat.Combat.toggleCombat
import com.bh.planners.api.getFlag
import org.bukkit.Bukkit
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.function.info
import taboolib.expansion.createHelper
import taboolib.platform.util.sendLang

@CommandHeader("combat")
object PlannersCombatCommand {


    @CommandBody
    val main = mainCommand {
        createHelper()
    }

    @CommandBody
    val switch = subCommand {
        dynamic("player") {
            suggestion<ProxyCommandSender> { sender, context -> Bukkit.getOnlinePlayers().map { it.name } }

            execute<ProxyCommandSender> { sender, context, argument ->
                val player = Bukkit.getPlayerExact(argument) ?: return@execute
                player.toggleCombat()
                player.sendLang("player-switch-combat", player.isCombatLocal)
            }
        }
    }

}