package com.bh.planners.core.kether.game

import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.PlannersAPI.plannersProfileIsLoaded
import com.bh.planners.api.common.Operator
import com.bh.planners.api.common.Operator.*
import com.bh.planners.core.kether.NAMESPACE
import com.bh.planners.core.kether.bukkitPlayer
import com.bh.planners.core.kether.execPlayer
import com.bh.planners.core.kether.nextSelectorOrNull
import com.bh.planners.core.module.mana.ManaManager
import org.bukkit.entity.Player
import taboolib.common5.Coerce
import taboolib.common5.cdouble
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
                execute(frame.bukkitPlayer() ?: return@thenApply, mode, amount)
            }
        }
        return CompletableFuture.completedFuture(null)
    }

    fun execute(player: Player, mode: Operator, amount: Double) {
        if (!player.plannersProfileIsLoaded) return
        val profile = player.plannersProfile
        when (mode) {
            ADD -> ManaManager.INSTANCE.addMana(profile, amount)
            TAKE -> ManaManager.INSTANCE.takeMana(profile, amount)
            SET -> ManaManager.INSTANCE.setMana(profile, amount)
            RESET -> ManaManager.INSTANCE.setMana(profile, ManaManager.INSTANCE.getMaxMana(profile))
        }
    }

    internal object Parser {

        /**
         * 操作目标法力值
         * mana [mode] [amount] [selector]
         * mana add 100 they "@self"
         * mana take 20 they "@self"
         * mana set 114514 they "@self"
         */
        @KetherParser(["mana"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {

            it.switch {
                case("add", "give") {
                    ActionMana(ADD, it.nextParsedAction(), it.nextSelectorOrNull())
                }

                case("take", "subtract") {
                    ActionMana(TAKE, it.nextParsedAction(), it.nextSelectorOrNull())
                }

                case("set") {
                    ActionMana(SET, it.nextParsedAction(), it.nextSelectorOrNull())
                }
                other {
                    error("error of case!")
                }
            }
        }
    }
}