package com.bh.planners.core.kether.enhance

import com.bh.planners.api.ManaCounter.addMana
import com.bh.planners.api.ManaCounter.setMana
import com.bh.planners.api.ManaCounter.takeMana
import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.core.kether.NAMESPACE
import com.bh.planners.core.kether.createTargets
import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionMana(
    val mode: Mode,
    val amount: ParsedAction<*>,
    val selector: ParsedAction<*>
) : ScriptAction<Void>() {

    enum class Mode {
        ADD, TAKE, SET
    }

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        frame.newFrame(amount).run<Any>().thenApply { amount ->
            frame.createTargets(selector).thenApply { container ->
                container.forEachPlayer {
                    val profile = this.plannersProfile
                    val value = Coerce.toDouble(amount)
                    when (mode) {
                        Mode.ADD -> profile.addMana(value)
                        Mode.TAKE -> profile.takeMana(value)
                        Mode.SET -> profile.setMana(value)
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
            val mode = when (it.expects("add, give, take, subtract, set")) {
                "add", "give" -> Mode.ADD
                "take", "subtract" -> Mode.TAKE
                "set" -> Mode.SET
                else -> error("error")
            }
            val amount = it.next(ArgTypes.ACTION)
            ActionMana(mode, amount, it.next(ArgTypes.ACTION))
        }
    }
}