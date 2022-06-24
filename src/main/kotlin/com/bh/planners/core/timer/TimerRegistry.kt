package com.bh.planners.core.timer

import com.bh.planners.core.kether.namespaces
import com.bh.planners.core.pojo.Context
import com.bh.planners.core.pojo.Skill
import org.bukkit.entity.Player
import org.bukkit.event.Event
import taboolib.common.LifeCycle
import taboolib.common.io.getInstance
import taboolib.common.io.runningClasses
import taboolib.common.platform.Awake
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.registerBukkitListener
import taboolib.common.platform.function.submit
import taboolib.module.kether.KetherShell
import taboolib.module.kether.printKetherErrorMessage

object TimerRegistry {

    private val map = mutableMapOf<String, Class<out Event>>()
    val triggers = mutableMapOf<String, Timer<*>>()

    val EMPTY = Skill.Empty()

    @Suppress("UNCHECKED_CAST")
    @Awake(LifeCycle.ENABLE)
    fun loadImplClass() {
        runningClasses.forEach {
            if (Timer::class.java.isAssignableFrom(it)) {
                (it.getInstance()?.get() as? Timer<*>)?.register()

            }
        }
    }

    fun <E : Event> Timer<E>.register() {
        triggers[name] = this
        registerBukkitListener(eventClazz, EventPriority.MONITOR, ignoreCancelled = false) { e ->
            val checkPlayer = this@register.check(e) ?: return@registerBukkitListener
            callTimer(this@register, checkPlayer, e)
        }
    }

    fun <E : Event> callTimer(timer: Timer<E>, sender: ProxyCommandSender, event: E) {
        val list = TimerDrive.templates.filter { timer.name in it.triggers }
        list.forEach { callTimer(timer, it, sender, event) }
    }

    fun <E : Event> callTimer(timer: Timer<E>, template: Template, sender: ProxyCommandSender, event: E) {
        if (template.action.isNotEmpty()) {
            when (template.async) {
                true -> submit(async = true) {
                    callTimerAction(timer, template, sender, event)
                }

                false -> callTimerAction(timer, template, sender, event)
            }
        }
    }

    fun <E : Event> callTimerAction(timer: Timer<E>, template: Template, sender: ProxyCommandSender, event: E) {
        try {
            KetherShell.eval(template.action, cacheScript = true, sender = sender, namespace = namespaces) {
                rootFrame().variables()["@Session"] = Context.Impl(sender, EMPTY)
                rootFrame().variables()["@Event"] = event
                timer.onStart(this, template, event)
            }
        } catch (e: Throwable) {
            e.printKetherErrorMessage()
        }
    }

}
