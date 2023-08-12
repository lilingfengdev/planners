package com.bh.planners.core.kether

import com.bh.planners.api.Counting
import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.maxLevel
import com.bh.planners.api.optAsync
import com.bh.planners.core.effect.Target.Companion.getEntity
import com.bh.planners.core.effect.Target.Companion.getLocation
import com.bh.planners.core.kether.common.CombinationKetherParser
import com.bh.planners.core.kether.common.MultipleKetherParser
import com.bh.planners.core.kether.common.simpleKetherNow
import com.bh.planners.core.module.mana.ManaManager
import taboolib.module.kether.*

@CombinationKetherParser.Used
object ActionMeta : MultipleKetherParser("meta"){

    val executor = object : MultipleKetherParser() {

        val name = simpleKetherNow("name") { executor().name }

        val uuid = simpleKetherNow("uuid") { bukkitTarget().getEntity()?.uniqueId }

        val location = simpleKetherNow("location","loc") {bukkitTarget().getLocation()  }

        val mana = simpleKetherNow("mana") { ManaManager.INSTANCE.getMana(bukkitPlayer()!!.plannersProfile) }

        val maxmana = simpleKetherNow("maxmana") {
            ManaManager.INSTANCE.getMaxMana(bukkitPlayer()!!.plannersProfile)
        }

    }

    val skill = object : MultipleKetherParser() {

        val name = simpleKetherNow { skill().name }

        val async = simpleKetherNow { skill().instance.option.async }

        val level = simpleKetherNow { skill().level }

        val maxlevel = simpleKetherNow { skill().maxLevel }

    }

    val origin = scriptParser {
        it.switch {
            case("to") {
                val action = it.nextSelectorOrNull()
                actionNow {
                    containerOrSender(action).thenAccept {
                        getContext().origin = it.firstLocationTarget()!!
                    }
                }
            }
            other {
                actionNow { origin() }
            }
        }
    }

}