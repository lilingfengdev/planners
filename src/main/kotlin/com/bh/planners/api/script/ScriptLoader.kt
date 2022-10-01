package com.bh.planners.api.script

import com.bh.planners.api.PlannersAPI
import com.bh.planners.core.kether.namespaces
import com.bh.planners.core.pojo.Session
import com.bh.planners.core.pojo.Skill
import com.google.common.collect.MultimapBuilder
import taboolib.module.kether.KetherScriptLoader
import taboolib.module.kether.Script
import taboolib.module.kether.ScriptContext
import taboolib.module.kether.ScriptService
import java.nio.charset.StandardCharsets

@Suppress("UnstableApiUsage")
object ScriptLoader {

    val ketherScriptLoader = KetherScriptLoader()

    val scripts = mutableMapOf<String, Script>()
    val runningScripts = MultimapBuilder.hashKeys().arrayListValues().build<String, ScriptContext>()

    fun autoLoad() {
        clear()
        PlannersAPI.skills.filter { it.actionMode == Skill.ActionMode.DEFAULT }.forEach {
            load(it)
        }
    }

    fun clear() {
        scripts.clear()
    }

    fun runScript(session: Session, func: (ScriptContext) -> Unit) {
        val script = scripts[session.skill.key] ?: return
        val scriptContext = ScriptContext.create(script, func)
        runningScripts.put(session.id, scriptContext)
        scriptContext.runActions().thenRunAsync({
            runningScripts.remove(session.id, scriptContext)
        }, ScriptService.executor)
    }

    fun invokeFunction(session: Session, name: String, func: (ScriptContext) -> Unit) {
        val script = scripts[session.skill.key] ?: return
        val scriptContext = ScriptContext.create(script) {
            session.open(this)
            func(this)
        }
        script.getBlock(name).ifPresent {
            it.actions.forEach {
                it.process(scriptContext.rootFrame())
            }
        }
    }

    fun load(skill: Skill) {
        scripts[skill.key] = ketherScriptLoader.load(ScriptService, skill.key, getBytes(skill), namespaces)
    }

    fun getBytes(skill: Skill): ByteArray {
        val texts = skill.action.split("\n")
        return texts.mapNotNull { if (it.trim().startsWith("#")) null else it }.joinToString("\n").toByteArray(
            StandardCharsets.UTF_8
        )
    }

}