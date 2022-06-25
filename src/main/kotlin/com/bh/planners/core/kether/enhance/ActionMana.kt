package com.bh.planners.core.kether.enhance

import com.bh.planners.api.ManaCounter.addMana
import com.bh.planners.api.ManaCounter.setMana
import com.bh.planners.api.ManaCounter.takeMana
import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.common.Operator
import com.bh.planners.core.kether.NAMESPACE
import com.bh.planners.core.kether.createTargets
import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionMana(
    val mode: Operator,
    val amount: ParsedAction<*>,
    val selector: ParsedAction<*>
) : ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        frame.newFrame(amount).run<Any>().thenApply { amount ->
            frame.createTargets(selector).thenApply { container ->
                container.forEachPlayer {
                    val profile = this.plannersProfile
                    val value = Coerce.toDouble(amount)
                    when (mode) {
                        Operator.ADD -> profile.addMana(value)
                        Operator.TAKE -> profile.takeMana(value)
                        Operator.SET -> profile.setMana(value)
                    }
                }
            }
        }
        return CompletableFuture.completedFuture(null)
    }

    internal object Parser {

        /**
         * 操作目标法力值
         * mana [mode] [amount] [selector]
         * mana add 100 "-@self"
         * mana take 20 "-@self"
         * mana set 114514 "-@self"
         */
        @KetherParser(["mana"], namespace = NAMESPACE)
        fun parser() = scriptParser {

            it.switch {
                case("add", "give") {
                    ActionMana(Operator.ADD, it.next(ArgTypes.ACTION), it.next(ArgTypes.ACTION))
                }

                case("take", "subtract") {
                    ActionMana(Operator.TAKE, it.next(ArgTypes.ACTION), it.next(ArgTypes.ACTION))
                }

                case("set") {
                    ActionMana(Operator.SET, it.next(ArgTypes.ACTION), it.next(ArgTypes.ACTION))
                }
                other {
                    error("error of case!")
                }
            }
        }
    }
}