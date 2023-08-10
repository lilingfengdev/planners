package com.bh.planners.command

import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.hasJob
import com.bh.planners.core.kether.bukkitPlayer
import com.bh.planners.core.module.mana.ManaManager
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.*
import taboolib.common5.cdouble
import taboolib.expansion.createHelper
import taboolib.platform.util.sendLang

@CommandHeader("plannersmana")
object PlannersManaCommand {


    @CommandBody
    val main = mainCommand {
        createHelper()
    }

    @CommandBody
    val give = subCommand {
        player {
            dynamic("value") {
                execute<ProxyCommandSender> { _, context, argument ->
                    val player = context.player("player").bukkitPlayer()!!
                    if (player.hasJob) {
                        ManaManager.INSTANCE.addMana(player.plannersProfile,argument.cdouble)
                        player.sendLang("player-get-mana", argument)
                    }
                }
            }
        }
    }

    @CommandBody
    val take = subCommand {
        player {
            dynamic("value") {
                execute<ProxyCommandSender> { _, context, argument ->
                    val player = context.player("player").bukkitPlayer()!!
                    if (player.hasJob) {
                        ManaManager.INSTANCE.takeMana(player.plannersProfile,argument.cdouble)
                        player.sendLang("player-take-mana", argument)
                    }
                }
            }
        }
    }


}