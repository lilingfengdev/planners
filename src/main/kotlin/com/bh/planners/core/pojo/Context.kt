package com.bh.planners.core.pojo

import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.core.kether.LazyGetter
import com.bh.planners.core.kether.namespaces
import com.bh.planners.core.pojo.data.DataContainer
import com.bh.planners.core.pojo.player.PlayerJob
import com.bh.planners.core.pojo.player.PlayerProfile
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.submit
import taboolib.module.kether.KetherShell
import taboolib.module.kether.printKetherErrorMessage

abstract class Context(val executor: ProxyCommandSender, val skill: Skill) {

    var closed = false

    val asPlayer: Player
        get() = executor.cast()

    val profile: PlayerProfile
        get() = asPlayer.plannersProfile

    open val playerSkill: PlayerJob.Skill
        get() = profile.getSkill(skill.key)!!

    val flags = DataContainer()

    val variables = skill.option.variables.associate { it.key to toLazyVariable(it) }

    private fun toLazyVariable(variable: Skill.Variable): LazyGetter<*> {
        return LazyGetter {
            try {
                KetherShell.eval(variable.expression, namespace = namespaces, sender = executor) {
                    rootFrame().variables()["@Session"] = this@Context
                    rootFrame().variables()["@Skill"] = playerSkill
                    variables.filter { it.key != variable.key }.forEach {
                        rootFrame().variables()[it.key] = it.value
                    }
                }.get()
            } catch (e: Throwable) {
                e.printKetherErrorMessage()
            }
        }
    }

    open class Impl(executor: ProxyCommandSender, skill: Skill) : Context(executor, skill) {}

}