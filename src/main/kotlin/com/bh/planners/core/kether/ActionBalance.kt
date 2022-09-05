package com.bh.planners.core.kether

import org.bukkit.block.Furnace
import org.bukkit.entity.Player
import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import taboolib.platform.compat.VaultService
import java.util.concurrent.CompletableFuture

class ActionBalance {


    class BalanceHas(val action: ParsedAction<*>) : ScriptAction<Boolean>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Boolean> {
            return frame.newFrame(action).run<Any>().thenApply {
                bridge.has(frame.script().sender!!.cast<Player>(), Coerce.toDouble(it))
            }
        }
    }

    class BalanceGet() : ScriptAction<Double>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Double> {
            return CompletableFuture.completedFuture(bridge.getBalance(frame.script().sender!!.cast<Player>()))
        }
    }

    class BalanceDeposit(val action: ParsedAction<*>) : ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            return frame.newFrame(action).run<Any>().thenAccept {
                bridge.depositPlayer(frame.script().sender!!.cast<Player>(), Coerce.toDouble(it))
            }
        }
    }

    class BalanceWithdraw(val action: ParsedAction<*>) : ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            return frame.newFrame(action).run<Any>().thenAccept {
                bridge.withdrawPlayer(frame.script().sender!!.cast<Player>(), Coerce.toDouble(it))
            }
        }
    }

    companion object {


        private val bridge by lazy { VaultService.economy ?: error("Not 'Vault economy' bridge found") }


        /**
         * balance has [value: action]
         * balance take/withdraw [value: action]
         * balance add/deposit [value: action]
         * balance get/look
         * balance
         */
        @KetherParser(["balance"], shared = true)
        fun parser() = scriptParser {
            it.switch {
                case("has") {
                    BalanceHas(it.nextParsedAction())
                }
                case("take", "withdraw") {
                    BalanceWithdraw(it.nextParsedAction())
                }
                case("add", "deposit") {
                    BalanceDeposit(it.nextParsedAction())
                }
                case("get", "look") {
                    BalanceGet()
                }
                other {
                    BalanceGet()
                }
            }
        }
    }


}