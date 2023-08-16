package com.bh.planners.core.pojo

import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.script.ScriptLoader
import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.getPlayer
import com.bh.planners.core.kether.LazyGetter
import com.bh.planners.util.safeAsync
import com.bh.planners.util.safeSync
import org.bukkit.GameMode
import taboolib.module.kether.ScriptContext
import taboolib.module.kether.runKether

open class Session(sender: Target, skill: Skill) : Context.Impl(sender, skill) {

    override val sourceId = skill.key

    val cooldown = variables["cooldown"]?.toLazyGetter() ?: LazyGetter { 0 }

    val mpCost = variables["mp"]?.toLazyGetter() ?: LazyGetter { 0 }

    fun cast() {

        if (sender.getPlayer()?.gameMode == GameMode.SPECTATOR) {
            return
        }

        if (skill.option.async) {
            safeAsync {
                run()
            }
        } else {
            safeSync {
                run()
            }
        }
    }


    private fun run() {
        // 简洁模式运行
        if (scriptMode == Skill.ActionMode.SIMPLE) {
            runKether {
                ScriptLoader.createScript(this) {
                    player?.plannersProfile?.runningScripts?.set(skill.key, this)
                    open(this)
                }
            }
        }
        // 常规模式运行
        else {
            ScriptLoader.runScript(this) {
                player?.plannersProfile?.runningScripts?.set(skill.key, it)
                open(it)
            }
        }
    }

    fun open(context: ScriptContext) {
        context.sender = proxySender
        context.rootFrame().variables()["@Context"] = this@Session
        variables.forEach {
            context.rootFrame().variables()[it.key] = it.value
        }
    }

}
