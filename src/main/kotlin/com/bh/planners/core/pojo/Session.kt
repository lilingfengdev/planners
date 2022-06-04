package com.bh.planners.core.pojo

import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.core.kether.LazyGetter
import com.bh.planners.core.kether.namespaces
import com.bh.planners.core.pojo.player.PlayerJob
import com.bh.planners.core.pojo.player.PlayerProfile
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.submit
import taboolib.module.kether.KetherShell
import taboolib.module.kether.printKetherErrorMessage

open class Session(val executor: ProxyCommandSender, val skill: Skill) {

    var closed = false

    val asPlayer: Player
        get() = executor.cast()

    val profile: PlayerProfile
        get() = asPlayer.plannersProfile

    val playerSkill: PlayerJob.Skill
        get() = profile.getSkill(skill.key)!!

    val variables = skill.option.variables.associate { it.key to toLazyVariable(it) }

    val cooldown = variables["cooldown"] ?: LazyGetter { 0 }

    val mpCost = variables["mp"] ?: LazyGetter { 0 }

    private fun toLazyVariable(variable: Skill.Variable): LazyGetter<*> {
        return LazyGetter {
            try {
                KetherShell.eval(variable.expression, namespace = namespaces, sender = executor) {
                    rootFrame().variables()["@Session"] = this@Session
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

    fun cast() {
        if (skill.option.async) {
            submit(async = true) { run() }
        } else {
            run()
        }
    }

    private fun run() {
        try {
            KetherShell.eval(skill.action, sender = executor, namespace = namespaces) {
                rootFrame().variables()["@Session"] = this@Session
                rootFrame().variables()["@Skill"] = playerSkill
                variables.forEach {
                    rootFrame().variables()[it.key] = it.value
                }
            }
        } catch (e: Throwable) {
            e.printKetherErrorMessage()
        }
    }
}
