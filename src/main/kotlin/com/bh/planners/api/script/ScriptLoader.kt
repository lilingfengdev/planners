package com.bh.planners.api.script

import com.bh.planners.api.PlannersAPI
import com.bh.planners.core.kether.namespaces
import com.bh.planners.core.kether.rootVariables
import com.bh.planners.core.pojo.Context
import com.bh.planners.core.pojo.Session
import com.bh.planners.core.pojo.Skill
import com.google.common.collect.MultimapBuilder
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.kether.*
import java.nio.charset.StandardCharsets
import java.util.concurrent.CompletableFuture

@Suppress("UnstableApiUsage")
object ScriptLoader {

    val ketherScriptLoader = KetherScriptLoader()

    val scripts = mutableMapOf<String, Script>()
    val runningScripts = MultimapBuilder.hashKeys().arrayListValues().build<String, ScriptData>()

    fun autoLoad() {
        scripts.clear()
        PlannersAPI.skills.filter { it.actionMode == Skill.ActionMode.DEFAULT }.forEach {
            load(it)
        }
    }

    fun runScript(session: Session, func: (ScriptContext) -> Unit) {
        val script = scripts[session.skill.key] ?: return
        val scriptContext = ScriptContext.create(script, func)
        runningScripts.put(session.id, ScriptData(session.executor, scriptContext))
        scriptContext.runActions().thenRunAsync({
            runningScripts.remove(session.id, scriptContext)
        }, ScriptService.executor)
    }

    fun createScript(session: Session, block: ScriptContext.() -> Unit): CompletableFuture<Any?> {
        return createScript(session) {
            block(this)
            runningScripts.put(session.id, ScriptData(session.executor,this))
            rootFrame().addClosable(AutoCloseable {
                runningScripts.remove(session.id, this)
            })
        }
    }

    fun createScript(context: Context.Impl,block: ScriptContext.() -> Unit) : CompletableFuture<Any?> {
        return createScript(context,context.skill.action,block)
    }

    fun createScript(context: Context,script: String,block: ScriptContext.() -> Unit) : CompletableFuture<Any?> {
        return KetherShell.eval(script, sender = context.executor, namespace = namespaces) {
            rootFrame().rootVariables()["@Context"] = context
            if (context is Context.Impl) {
                context.variables.forEach {
                    rootFrame().variables()[it.key] = it.value
                }
            }
            block(this)
        }
    }

    @SubscribeEvent
    fun e(e: PlayerQuitEvent) {
        runningScripts.values().forEach {
            if (it.sender.castSafely<Player>() == e.player) {
                it.close()
            }
        }
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

    class ScriptData(val sender: ProxyCommandSender, val context: ScriptContext) {

        fun close() {
            context.terminate()
        }

    }

}