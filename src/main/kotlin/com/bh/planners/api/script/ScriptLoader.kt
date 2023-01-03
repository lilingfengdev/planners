package com.bh.planners.api.script

import com.bh.planners.api.PlannersAPI
import com.bh.planners.core.kether.namespaces
import com.bh.planners.core.kether.rootVariables
import com.bh.planners.core.pojo.Context
import com.bh.planners.core.pojo.Session
import com.bh.planners.core.pojo.Skill
import com.bh.planners.core.timer.Template
import com.bh.planners.core.timer.TimerDrive
import com.bh.planners.core.timer.TimerRegistry
import com.google.common.collect.MultimapBuilder
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.kether.*
import java.nio.charset.StandardCharsets
import java.sql.PreparedStatement
import java.util.UUID
import java.util.concurrent.CompletableFuture

@Suppress("UnstableApiUsage")
object ScriptLoader {

    val ketherScriptLoader = KetherScriptLoader()

    val scripts = mutableMapOf<String, Script>()
    val runningScripts = MultimapBuilder.hashKeys().arrayListValues().build<String, ScriptData>()

    fun autoLoad() {
        scripts.clear()
        PlannersAPI.skills.forEach {
            if (it.script.mode == Skill.ActionMode.DEFAULT) {
                load(it)
            }
        }
        TimerDrive.templates.forEach {
            if (it.script.mode == Skill.ActionMode.DEFAULT) {
                load(it)
            }
        }
    }

    fun runScript(context: Context, func: (ScriptContext) -> Unit = {}) {
        val script = scripts[context.id] ?: return
        val scriptContext = ScriptContext.create(script, func)
        runningScripts.put(context.id, ScriptData(context.executor, scriptContext))
        scriptContext.runActions().thenRunAsync({
            runningScripts.remove(context.id, scriptContext)
        }, ScriptService.asyncExecutor)
    }

    fun createScript(session: Session, block: ScriptContext.() -> Unit = {}): CompletableFuture<Any?> {
        return createScript(session as Context.Impl) {
            block(this)
            runningScripts.put(session.id, ScriptData(session.executor, this))
            rootFrame().addClosable(AutoCloseable {
                runningScripts.remove(session.id, this)
            })
        }
    }

    fun createScript(context: Context.Impl, block: ScriptContext.() -> Unit = {}): CompletableFuture<Any?> {
        return createScript(context, context.skill.script.action, block)
    }

    fun createFunctionScript(context: Context, inputs: List<String>, block: ScriptContext.() -> Unit = {}): List<String> {
        return inputs.map { createFunctionScript(context, it, block) }
    }

    fun createFunctionScript(context: Context, input: String, block: ScriptContext.() -> Unit = {}): String {
        return KetherFunction.parse(input) {
            this.rootFrame().rootVariables()["@Context"] = context
            if (context is Context.Impl) {
                context.variables.forEach {
                    this.rootFrame().variables()[it.key] = it.value
                }
            }
            block(this)
        }
    }

    fun createScript(context: Context, script: String, block: ScriptContext.() -> Unit = {}): CompletableFuture<Any?> {
        return KetherShell.eval(script, sender = context.executor, namespace = namespaces) {
            this.id = UUID.randomUUID().toString()
            this.rootFrame().rootVariables()["@Context"] = context
            if (context is Context.Impl) {
                context.variables.forEach {
                    this.rootFrame().variables()[it.key] = it.value
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

    fun invokeFunction(context: Context.SourceImpl, name: String, func: (ScriptContext) -> Unit = {}) {

        val sourceId = context.sourceId

        val script = scripts[sourceId] ?: return
        val scriptContext = ScriptContext.create(script) {
            this.rootFrame().variables()["@Context"] = context
            (context as? Session)?.open(this)
            func(this)
        }
        script.getBlock(name).ifPresent {
            it.actions.forEach {
                it.process(scriptContext.rootFrame())
            }
        }
    }

    fun load(skill: Skill) {
        runKether {
            scripts[skill.key] = ketherScriptLoader.load(ScriptService, skill.key, getBytes(skill), namespaces)
        }
    }

    fun load(template: Template) {
        runKether {
            scripts[template.id] = ketherScriptLoader.load(ScriptService,template.id, getBytes(template.script.action))
        }
    }

    fun getBytes(text: String) : ByteArray {
        val texts = text.split("\n")
        return texts.mapNotNull { if (it.trim().startsWith("#")) null else it }.joinToString("\n").toByteArray(
            StandardCharsets.UTF_8
        )
    }

    fun getBytes(skill: Skill): ByteArray {
        return getBytes(skill.script.action)
    }

    class ScriptData(val sender: ProxyCommandSender, val context: ScriptContext) {

        fun close() {
            context.terminate()
        }

    }

}