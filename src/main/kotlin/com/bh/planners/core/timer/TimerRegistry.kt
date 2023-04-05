package com.bh.planners.core.timer

import com.bh.planners.api.common.Plugin
import com.bh.planners.api.event.ISource
import com.bh.planners.api.script.ScriptLoader
import com.bh.planners.core.effect.Target
import com.bh.planners.core.pojo.Skill
import com.bh.planners.util.runKetherThrow
import org.bukkit.Bukkit
import org.bukkit.event.Event
import taboolib.common.LifeCycle
import taboolib.common.io.getInstance
import taboolib.common.io.runningClasses
import taboolib.common.platform.Awake
import taboolib.common.platform.function.registerBukkitListener
import taboolib.common.platform.function.submit
import taboolib.module.kether.runKether

object TimerRegistry {

    private val map = mutableMapOf<String, Class<out Event>>()
    val triggers = mutableMapOf<String, Timer<*>>()

    val EMPTY = Skill.Empty()

    @Suppress("UNCHECKED_CAST")
    @Awake(LifeCycle.ENABLE)
    fun loadImplClass() {
        runningClasses.forEach {
            if (Timer::class.java.isAssignableFrom(it)) {

                if (it.isAssignableFrom(Plugin::class.java)) {
                    val annotation = it.getAnnotation(Plugin::class.java)
                    if (!Bukkit.getPluginManager().isPluginEnabled(annotation.name)) {
                        return@forEach
                    }
                }

                try {
                    (it.getInstance()?.get() as? Timer<*>)?.register()
                } catch (_: NoClassDefFoundError) {

                }
            }
        }
    }

    fun <E : Event> Timer<E>.register() {
        triggers[name] = this
        registerBukkitListener(eventClazz, this.priority, ignoreCancelled) { e ->
            val checkPlayer = this@register.check(e) ?: return@registerBukkitListener
            callTimer(this@register, checkPlayer, e)
        }

    }

    fun <E : Event> callTimer(timer: Timer<E>, sender: Target, event: E) {

        // 精确检索
        val id = (event as? ISource)?.id() ?: "*"

        val list = TimerDrive.templates.filter { timer.name in it.triggers && (id == "*" || id == it.id) }
        list.forEach { callTimer(timer, it, sender, event) }
    }

    fun <E : Event> callTimer(timer: Timer<E>, template: Template, sender: Target, event: E) {
        if (template.script.action.isNotEmpty()) {
            when (template.async) {
                true -> submit(async = true) {
                    callTimerAction(timer, template, sender, event)
                }

                false -> callTimerAction(timer, template, sender, event)
            }
        }
    }

    fun <E : Event> callTimerAction(timer: Timer<E>, template: Template, sender: Target, event: E) {

        val context = TimerContext(sender, template)
        if (template.script.mode == Skill.ActionMode.SIMPLE) {
            runKetherThrow(context) {
                ScriptLoader.createScript(context, template.script.action) {
                    rootFrame().variables()["@Event"] = event
                    timer.onStart(this, template, event)
                }
            }
        } else {
            runKetherThrow(context) {
                ScriptLoader.runScript(context) {
                    it.rootFrame().variables()["@Event"] = event
                    timer.onStart(it, template, event)
                }
            }
        }

    }

}
