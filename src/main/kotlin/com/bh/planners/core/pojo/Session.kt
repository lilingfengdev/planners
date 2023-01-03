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
        if (scriptMode == Skill.ActionMode.SIMPLE) {
            runKether {
                ScriptLoader.createScript(this) {
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
        variables.forEach {
            context.rootFrame().variables()[it.key] = it.value
        }
    }

}
