package com.bh.planners.core.kether.game

import com.bh.planners.api.ManaCounter.addMana
import com.bh.planners.api.ManaCounter.setMana
import com.bh.planners.api.ManaCounter.takeMana
import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.PlannersAPI.plannersProfileIsLoaded
import com.bh.planners.api.common.Operator
import com.bh.planners.core.kether.*
import org.bukkit.entity.Player
import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionMana(val mode: Operator, val amount: ParsedAction<*>, val selector: ParsedAction<*>?) :
    ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        frame.newFrame(amount).run<Any>().thenApply {
            val amount = Coerce.toDouble(it)
            if (selector != null) {
                frame.execPlayer(selector) { execute(this, mode, amount) }
            } else {
                execute(frame.asPlayer() ?: return@thenApply, mode, amount)
            }
        }
        return CompletableFuture.completedFuture(null)
    }

    fun execute(player: Player, mode: Operator, amount: Double) {
        if (!player.plannersProfileIsLoaded) return
        val profile = player.plannersProfile
        val value = Coerce.toDouble(amount)
        when (mode) {
            Operator.ADD -> profile.addMana(value)
            Operator.TAKE -> profile.takeMana(value)
            Operator.SET -> profile.setMana(value)
        }
    }

    internal object Parser {

        /**
         * 操作目标法力值
         * mana [mode] [amount] [selector]
         * mana add 100 "-@self"
         * mana take 20 "-@self"
         * mana set 114514 "-@self"
         */
        @KetherParser(["mana"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {

            it.switch {
                case("add", "give") {
                    ActionMana(Operator.ADD, it.next(ArgTypes.ACTION), it.selectorAction())
                }

                case("take", "subtract") {
                    ActionMana(Operator.TAKE, it.next(ArgTypes.ACTION), it.selectorAction())
                }

                case("set") {
                    ActionMana(Operator.SET, it.next(ArgTypes.ACTION), it.selectorAction())
                }
                other {
                    error("error of case!")
                }
            }
        }
    }
}