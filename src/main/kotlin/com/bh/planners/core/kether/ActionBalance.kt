package com.bh.planners.core.kether

import com.bh.planners.core.kether.common.CombinationKetherParser
import com.bh.planners.core.kether.common.MultipleKetherParser
import org.bukkit.entity.Player
import taboolib.common.util.unsafeLazy
import taboolib.common5.Coerce
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import taboolib.platform.compat.VaultService
import java.util.concurrent.CompletableFuture

@CombinationKetherParser.Use
object ActionBalance : MultipleKetherParser("balance") {

    private val hooked by unsafeLazy {
        VaultService.economy!!
    }
    val has = case {
        combinationParser {
            it.group(double(), containerOrSender()).apply(it) { value, container ->
                now {
                    container.forEachPlayer {
                        hooked.has(this, value)
                    }
                }
            }
        }
    }

    val get = case {
        combinationParser {
            it.group(double(), containerOrSender()).apply(it) { value, container ->
                now {
                    val player = container.firstBukkitPlayer() ?: this.bukkitPlayer()!!
                    hooked.getBalance(player)
                }
            }
        }
    }

    val deposit = case("add") {
        combinationParser {
            it.group(double(), containerOrSender()).apply(it) { value, container ->
                now {
                    container.forEachPlayer {
                        hooked.depositPlayer(this, value)
                    }
                }
            }
        }
    }

    val withdraw = case("take") {
        combinationParser {
            it.group(double(), containerOrSender()).apply(it) { value, container ->
                now {
                    container.forEachPlayer {
                        hooked.withdrawPlayer(this, value)
                    }
                }
            }
        }
    }


}