package com.bh.planners.core.kether.game

import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.core.effect.Target
import com.bh.planners.core.kether.common.CombinationKetherParser
import com.bh.planners.core.kether.common.KetherHelper
import com.bh.planners.core.kether.common.KetherHelper.containerOrSender
import com.bh.planners.core.kether.common.MultipleKetherParser
import com.bh.planners.core.module.mana.ManaManager

/**
 * 魔法值操作
 */
@CombinationKetherParser.Used
object ActionMana : MultipleKetherParser("mana") {

    // mana add 100 they "@self"
    val add = parser { container, value ->
        container.forEachPlayer {
            ManaManager.INSTANCE.addMana(plannersProfile, value)
        }
    }

    // mana take 100 they "@self"
    val take = parser { container, value ->
        container.forEachPlayer {
            ManaManager.INSTANCE.takeMana(plannersProfile, value)
        }
    }

    // mana set 100 they "@self"
    val set = parser { container, value ->
        container.forEachPlayer {
            ManaManager.INSTANCE.setMana(plannersProfile, value)
        }
    }

    // mana reset 100 they "@self"
    val reset = parser { container, value ->
        container.forEachPlayer {
            ManaManager.INSTANCE.setMana(plannersProfile, ManaManager.INSTANCE.getMaxMana(plannersProfile))
        }
    }

    @CombinationKetherParser.Ignore
    fun parser(block: (container: Target.Container, value: Double) -> Unit) = KetherHelper.simpleKetherParser<Unit> {
        it.group(double(), containerOrSender()).apply(it) { value, container ->
            now { block(container, value) }
        }
    }

}