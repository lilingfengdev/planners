package com.bh.planners.core.kether

import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.maxLevel
import com.bh.planners.core.effect.Target.Companion.getEntity
import com.bh.planners.core.effect.Target.Companion.getLocation
import com.bh.planners.core.kether.common.CombinationKetherParser
import com.bh.planners.core.kether.common.KetherHelper.containerOrSender
import com.bh.planners.core.kether.common.KetherHelper.simpleKetherNow
import com.bh.planners.core.kether.common.KetherHelper.simpleKetherParser
import com.bh.planners.core.kether.common.MultipleKetherParser
import com.bh.planners.core.module.mana.ManaManager

@CombinationKetherParser.Used
object ActionMeta : MultipleKetherParser("meta") {

    val executor = object : MultipleKetherParser() {

        val name = simpleKetherNow("name") { executor().name }

        val uuid = simpleKetherNow("uuid") { bukkitTarget().getEntity()?.uniqueId }

        val location = simpleKetherNow("location", "loc") { bukkitTarget().getLocation() }

        val mana = simpleKetherNow("mana") { ManaManager.INSTANCE.getMana(bukkitPlayer()!!.plannersProfile) }

        val maxmana = simpleKetherNow("maxmana") {
            ManaManager.INSTANCE.getMaxMana(bukkitPlayer()!!.plannersProfile)
        }

    }

    val skill = object : MultipleKetherParser() {

        val name = simpleKetherNow { skill().name }

        val async = simpleKetherNow { skill().instance.option.async }

        val level = simpleKetherNow { skill().level }

        val maxlevel = simpleKetherNow("max-level", "level-max", "level-cap", "cap-level") {
            skill().maxLevel
        }

        val shortcut = simpleKetherNow { skill().shortcutKey }

        val natural = simpleKetherNow { skill().instance.option.isNatural }

        val bind = simpleKetherNow { skill().instance.option.isBind }

        val naturallevel = simpleKetherNow("natural-level", "level-natural") { skill().instance.option.naturalLevel }

    }

    val origin = object : MultipleKetherParser() {

        val to = simpleKetherParser<Unit>("set") {
            it.group(containerOrSender()).apply(it) { container ->
                now { getContext().origin = container.firstTarget()!! }
            }
        }

        val main = simpleKetherNow { getContext().origin }

    }

}