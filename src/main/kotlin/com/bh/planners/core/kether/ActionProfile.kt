package com.bh.planners.core.kether

import com.bh.planners.api.EntityAPI.getFlag
import com.bh.planners.api.EntityAPI.getDataContainer
import com.bh.planners.api.addPoint
import com.bh.planners.api.setFlag
import com.bh.planners.api.setPoint
import com.bh.planners.core.kether.common.*
import com.bh.planners.core.module.mana.ManaManager
import com.bh.planners.core.pojo.data.Data
import taboolib.common5.Coerce
import taboolib.common5.cdouble
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

@CombinationKetherParser.Used
object ActionProfile : MultipleKetherParser("profile"){

    // profile data <id>
    val data = object : ParameterKetherParser("flag") {

        // profile data <id>
        val main = simpleKetherParser<Any?> {
            it.group(command("default", "def", then = any()).option()).apply(it) { value ->
                argumentNow { bukkitPlayer()?.getDataContainer()?.get(it.toString()) ?: value }
            }
        }

        // profile data <id> set <value>
        val set = simpleKetherParser<Unit> {
            it.group(any(), command("timeout", "time", then = long()).option().defaultsTo(0L)).apply(it) { value, timeout ->
                argumentNow { id ->
                    bukkitPlayer()?.getDataContainer()?.set(id.toString(), Data(value!!, timeout)) ?: Unit
                }
            }
        }

        // profile data <id> add <value>
        val add = simpleKetherParser<Any?> {
            it.group(any()).apply(it) { value ->
                argumentNow { id ->
                    val dataContainer = bukkitPlayer()?.getDataContainer()
                    if (dataContainer == null) {
                        return@argumentNow null
                    }
                    val data = dataContainer[id.toString()] ?: Data(0)
                    data.increaseAny(value ?: 0)
                    dataContainer.update(id.toString(), data)
                    data
                }
            }
        }

        // profile data <id> has
        val has = argumentKetherNow {
            bukkitPlayer()?.getDataContainer()?.containsKey(it!!.toString())
        }

    }

    // profile mana
    val mana = object : MultipleKetherParser() {

        // profile mana
        val main = simpleKetherNow { ManaManager.INSTANCE.getMana(senderPlannerProfile()!!) }

        // profile mana take <value>
        val take = simpleKetherParser<Unit> {
            it.group(double()).apply(it) { value ->
                now {
                    ManaManager.INSTANCE.takeMana(senderPlannerProfile()!!, value)
                }
            }
        }

        // profile mana val <value>
        val add = simpleKetherParser<Unit> {
            it.group(double()).apply(it) { value ->
                now {
                    ManaManager.INSTANCE.takeMana(senderPlannerProfile()!!, value)
                }
            }
        }
    }


    val healthpercent = simpleKetherNow("health-percent") {
        try {
            bukkitPlayer()!!.health / bukkitPlayer()!!.maxHealth
        } catch (_: java.lang.Exception) {
            0
        }
    }

    /**
     * profile manapercent
     * profile mana-percent
     */
    val manapercent = simpleKetherNow("health-percent") {
        val profile = senderPlannerProfile()!!
        try {
            ManaManager.INSTANCE.getMana(profile) / ManaManager.INSTANCE.getMaxMana(profile)
        } catch (_: java.lang.Exception) {
            0
        }
    }

    /**
     * profile maxmana
     * profile max-mana
     */
    val maxmana = simpleKetherNow("max-mana") {
        ManaManager.INSTANCE.getMana(senderPlannerProfile()!!)
    }

    // profile point
    val point = simpleKetherNow {
        senderPlannerProfile()!!.point
    }

    // profile job
    val job = simpleKetherNow {
        senderPlannerProfile()!!.job?.jobKey
    }

    // profile level
    val level = simpleKetherNow {
        senderPlannerProfile()!!.job?.level ?: -1
    }

    // profile exp
    val exp = simpleKetherNow {
        senderPlannerProfile()!!.experience
    }

    // profile maxexp
    val maxexp = simpleKetherNow {
        senderPlannerProfile()!!.maxExperience
    }

    /**
     * profile exppercent
     * profile exp-percent
     */
    val exppercent = simpleKetherNow("exp-percent") {
        try {
            senderPlannerProfile()!!.experience / senderPlannerProfile()!!.maxExperience
        } catch (_: Exception) {
            0.0
        }
    }
}