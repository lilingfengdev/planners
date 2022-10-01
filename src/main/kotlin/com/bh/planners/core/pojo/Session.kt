package com.bh.planners.core.pojo

import com.bh.planners.api.script.ScriptLoader
import com.bh.planners.core.kether.LazyGetter
import com.bh.planners.core.kether.namespaces
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.submit
import taboolib.module.kether.KetherShell
import taboolib.module.kether.ScriptContext
import taboolib.module.kether.runKether
import java.util.UUID

open class Session(executor: ProxyCommandSender, skill: Skill) : Context.Impl(executor, skill) {


    val id = UUID.randomUUID().toString()

    val cooldown = variables["cooldown"] ?: LazyGetter { 0 }

    val mpCost = variables["mp"] ?: LazyGetter { 0 }

    fun cast() {
        if (skill.option.async) {
            submit(async = true) { run() }
        } else {
            run()
        }
    }

    private fun run() {
        // 简洁模式运行
        if (skill.actionMode == Skill.ActionMode.SIMPLE) {
            runKether {
                KetherShell.eval(skill.action, sender = executor, namespace = namespaces) {
                    open(this)
                }
            }
        }
        // 常规模式运行
        else {
            ScriptLoader.runScript(this) {
                open(it)
            }
        }
    }

    fun open(context: ScriptContext) {
        context.sender = executor
        context.rootFrame().variables()["@Context"] = this@Session
        context.rootFrame().variables()["@Skill"] = playerSkill
        variables.forEach {
            context.rootFrame().variables()[it.key] = it.value
        }
    }

}
