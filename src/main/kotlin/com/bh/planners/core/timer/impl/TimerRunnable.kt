package com.bh.planners.core.timer.impl

import com.bh.planners.core.timer.*
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.event.Event
import taboolib.common.platform.Schedule
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.kether.ScriptContext
import taboolib.platform.type.BukkitProxyEvent

object TimerRunnable : AbstractTimer<TimerRunnable.TimerRunnableEvent>() {

    override val eventClazz: Class<out Event>
        get() = TimerRunnableEvent::class.java

    private val cache = mutableMapOf<String, Long>()

    @Schedule(period = 1, async = true)
    fun run() {
        getTimers<TimerRunnable>().filter { isClosed(it) }.forEach {
            mark(it)
            TimerRunnableEvent(it).call()
        }
    }

    @SubscribeEvent
    fun e(e: TimerRunnableEvent) {
        callTimer(e, e.timer)
    }

    fun mark(timer: TimerRunnable) {
        cache[timer.id] = System.currentTimeMillis()
    }

    fun isClosed(timer: TimerRunnable): Boolean {
        if (!cache.containsKey(timer.id)) return true
        return System.currentTimeMillis() >= cache[timer.id]!! + timer.period
    }


    fun Template.period(): Long {
        return root.getLong("__option__.period", 20L)
    }

    class TimerRunnableEvent(val player: Player, val template: Template) : BukkitProxyEvent()

    override fun onStart(context: ScriptContext, e: TimerRunnableEvent) {
        context["name"] = e.template.id
    }

    override fun check(e: TimerRunnableEvent): Player? {
        return e.player
    }

}
