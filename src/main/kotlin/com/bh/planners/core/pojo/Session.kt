package com.bh.planners.core.pojo

import com.bh.planners.core.kether.LazyGetter
import com.bh.planners.core.kether.namespaces
import com.bh.planners.core.pojo.data.DataContainer
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.submit
import taboolib.module.kether.KetherShell
import taboolib.module.kether.printKetherErrorMessage
import java.util.concurrent.Executors

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
