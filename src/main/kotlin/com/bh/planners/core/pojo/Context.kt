package com.bh.planners.core.pojo

import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.script.ScriptLoader
import com.bh.planners.core.kether.LazyGetter
import com.bh.planners.core.kether.namespaces
import com.bh.planners.core.pojo.data.DataContainer
import com.bh.planners.core.pojo.player.PlayerJob
import com.bh.planners.core.pojo.player.PlayerProfile
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.submit
import taboolib.module.kether.KetherShell
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.printKetherErrorMessage
import taboolib.module.kether.runKether

abstract class Context(val executor: ProxyCommandSender) {

    var closed = false

    val asPlayer: Player
        get() = executor.cast()

    val profile: PlayerProfile
        get() = asPlayer.plannersProfile

    val flags = DataContainer()

    protected fun toLazyVariable(variable: Skill.Variable): LazyGetter<*> {
        return LazyGetter {
            runKether {
                ScriptLoader.createScript(this, variable.expression) { }.get()
            }
        }
    }

    open class Impl0(executor: ProxyCommandSender) : Context(executor) {

    }

    open class Impl1(executor: ProxyCommandSender, skill: Skill, val level: Int) : Session(executor, skill) {

        override val playerSkill = PlayerJob.Skill(-1, skill.key, level, null)

    }

    open class Impl(executor: ProxyCommandSender, val skill: Skill) : Context(executor) {

        open val playerSkill: PlayerJob.Skill
            get() = profile.getSkill(skill.key)!!

        val variables = skill.option.variables.associate { it.key to toLazyVariable(it) }

    }

}