package com.bh.planners.core.kether

import com.bh.planners.core.effect.Target.Companion.getPlayer
import com.bh.planners.core.kether.common.CombinationKetherParser
import com.bh.planners.core.kether.common.MultipleKetherParser
import com.bh.planners.core.kether.common.containerOrSender
import com.bh.planners.core.kether.common.simpleKetherParser
import taboolib.common.util.unsafeLazy
import taboolib.module.kether.*
import taboolib.platform.compat.VaultService

@CombinationKetherParser.Used
object ActionBalance : MultipleKetherParser("balance") {

    private val hooked by unsafeLazy { VaultService.economy!! }

    val has = simpleKetherParser<Boolean> {
        it.group(double(), containerOrSender()).apply(it) { value, container ->
            now {
                container.mapNotNull { it.getPlayer() }.all { hooked.has(it,value) }
            }
        }
    }
    
    val get = simpleKetherParser<Double> {
        it.group(double(), containerOrSender()).apply(it) { value, container ->
            now {
                val player = container.firstBukkitPlayer() ?: this.bukkitPlayer()!!
                hooked.getBalance(player)
            }
        }
    }

    val deposit = simpleKetherParser<Unit> {
        it.group(double(), containerOrSender()).apply(it) { value, container ->
            now {
                container.forEachPlayer {
                    hooked.depositPlayer(this, value)
                }
            }
        }
    }

    val withdraw = simpleKetherParser<Unit> {
        it.group(double(), containerOrSender()).apply(it) { value, container ->
            now {
                container.forEachPlayer {
                    hooked.withdrawPlayer(this, value)
                }
            }
        }
    }


}