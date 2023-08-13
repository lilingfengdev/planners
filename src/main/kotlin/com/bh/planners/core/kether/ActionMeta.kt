package com.bh.planners.core.kether

import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.maxLevel
import com.bh.planners.core.effect.Target.Companion.getEntity
import com.bh.planners.core.effect.Target.Companion.getLocation
import com.bh.planners.core.kether.common.CombinationKetherParser
import com.bh.planners.core.kether.common.MultipleKetherParser
import com.bh.planners.core.kether.common.simpleKetherNow
import com.bh.planners.core.module.mana.ManaManager
import taboolib.module.kether.actionNow
import taboolib.module.kether.scriptParser
import taboolib.module.kether.switch

@CombinationKetherParser.Used
object ActionMeta : MultipleKetherParser("meta"){

    val executor = object : MultipleKetherParser() {

        val name = simpleKetherNow<Any>("name") { executor().name }

        val uuid = simpleKetherNow<Any>("uuid") { bukkitTarget().getEntity()?.uniqueId }

        val location = simpleKetherNow<Any>("location","loc") {bukkitTarget().getLocation()  }

        val mana = simpleKetherNow<Any>("mana") { ManaManager.INSTANCE.getMana(bukkitPlayer()!!.plannersProfile) }

        val maxmana = simpleKetherNow<Any>("maxmana") {
            ManaManager.INSTANCE.getMaxMana(bukkitPlayer()!!.plannersProfile)
        }

    }

    val skill = object : MultipleKetherParser() {

        val name = simpleKetherNow<Any> { skill().name }

        val async = simpleKetherNow<Any> { skill().instance.option.async }

        val level = simpleKetherNow<Any> { skill().level }

        val maxlevel = simpleKetherNow<Any> { skill().maxLevel }

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