package com.bh.planners.core.kether

import com.bh.planners.api.EntityAPI.getDataContainer
import com.bh.planners.core.kether.common.CombinationKetherParser
import com.bh.planners.core.kether.common.KetherHelper.simpleKetherNow
import com.bh.planners.core.kether.common.KetherHelper.simpleKetherParser
import com.bh.planners.core.kether.common.MultipleKetherParser
import com.bh.planners.core.kether.common.ParameterKetherParser
import com.bh.planners.core.module.mana.ManaManager
import com.bh.planners.core.pojo.data.Data
import org.bukkit.attribute.Attribute
import taboolib.module.kether.*

@CombinationKetherParser.Used
object ActionProfile : MultipleKetherParser("profile") {

    val data = object : ParameterKetherParser("flag") {

        val add = argumentKetherParser { argument ->
            val action = this.nextParsedAction()
            actionNow {
                run(argument).str { argument ->
                    run(action).thenAccept { value ->
                        val dataContainer = bukkitPlayer()?.getDataContainer() ?: return@thenAccept
                        val data = dataContainer[argument] ?: Data(0)
                        data.increaseAny(value ?: 0)
                        dataContainer.update(argument, data)
                    }
                }
            }
        }

        val to = argumentKetherParser("set") { argument ->
            val action = this.nextParsedAction()
            val timeout = this.nextOptionalParsedAction(arrayOf("timeout", "time"), 0L)!!
            actionNow {
                run(argument).str { argument ->
                    run(action).thenApply { value ->
                        run(timeout).long { timeout ->
                            bukkitPlayer()?.getDataContainer()?.set(argument, Data(value!!, timeout)) ?: Unit
                        }
                    }
                }
            }
        }

        val get = argumentKetherParser { argument ->
            val default = this.nextOptionalParsedAction(arrayOf("default","def"), "null")!!
            actionFuture {
                run(argument).str { argument ->
                    run(default).thenApply { default ->
                        it.complete(bukkitPlayer()?.getDataContainer()?.get(argument) ?: default)
                    }
                }
            }
        }

        val main = get

        val has = argumentKetherParser { argument ->
            actionFuture {
                run(argument).str { argument ->
                    it.complete(bukkitPlayer()?.getDataContainer()?.containsKey(argument))
                }
            }
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
            bukkitPlayer()!!.health / (bukkitPlayer()!!.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: bukkitPlayer()!!.health)
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
     * profile mana-max
     */
    val maxmana = simpleKetherNow("max-mana", "mana-max") {
        ManaManager.INSTANCE.getMaxMana(senderPlannerProfile()!!)
    }

    /**
     * profile regainmana
     * profile regain-mana
     * profile mana-regain
     */
    val regainmana = simpleKetherNow("regain-mana", "mana-regain") {
        ManaManager.INSTANCE.getRegainMana(senderPlannerProfile()!!)
    }

    // profile point
    val point = simpleKetherNow {
        senderPlannerProfile()!!.point
    }

    // profile job
    val job = simpleKetherNow {
        senderPlannerProfile()!!.job?.name
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
    val maxexp = simpleKetherNow("max-exp", "exp-max") {
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