package com.bh.planners.core.kether

import com.bh.planners.core.effect.Target.Companion.getPlayer
import com.bh.planners.core.kether.common.CombinationKetherParser
import com.bh.planners.core.kether.common.KetherHelper.containerOrSender
import com.bh.planners.core.kether.common.KetherHelper.simpleKetherParser
import com.bh.planners.core.kether.common.MultipleKetherParser
import taboolib.common.util.unsafeLazy
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
                val player = container.firstPlayer() ?: this.bukkitPlayer()!!
                hooked.getBalance(player)
            }
        }
    }

    val deposit = simpleKetherParser<Unit>("add") {
        it.group(double(), containerOrSender()).apply(it) { value, container ->
            now {
                container.forEachPlayer {
                    hooked.depositPlayer(this, value)
                }
            }
        }
    }

    val withdraw = simpleKetherParser<Unit>("take") {
        it.group(double(), containerOrSender()).apply(it) { value, container ->
            now {
                container.forEachPlayer {
                    hooked.withdrawPlayer(this, value)
                }
            }
        }
    }


}