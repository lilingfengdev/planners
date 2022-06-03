package com.bh.planners.core.timer.impl

import com.bh.planners.core.timer.*
import com.bh.planners.core.timer.TimerDrive.getTemplates
import org.bukkit.Bukkit
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.Schedule
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.console
import taboolib.platform.type.BukkitProxyEvent

object TimerRunnable : AbstractTimer<TimerRunnable.TimerRunnableEvent>() {

    override val eventClazz: Class<TimerRunnableEvent>
        get() = TimerRunnableEvent::class.java

    private val cache = mutableMapOf<String, Long>()

    @Schedule(period = 1, async = true)
    fun run() {
        getTemplates(this).filter { isClosed(it) }.forEach {
            mark(it)
            if (it.sender == "console") {
                TimerRunnableEvent(console(), it).call()
            } else if (it.sender == "player") {
                Bukkit.getOnlinePlayers().forEach { player ->
                    TimerRunnableEvent(adaptPlayer(player), it).call()
                }
            }

        }
    }

    private fun mark(template: Template) {
        cache[template.id] = System.currentTimeMillis()
    }

    private fun isClosed(template: Template): Boolean {
        if (!cache.containsKey(template.id)) return true
        return System.currentTimeMillis() >= cache[template.id]!! + template.period()
    }


    private fun Template.period(): Long {
        return root.getLong("__option__.period", 20L)
    }

    override fun check(e: TimerRunnableEvent): ProxyCommandSender? {
        return e.sender
    }

    override val name: String
        get() = "runnable"

    val Template.sender: String
        get() = root.getString("__option__.sender", "player")!!

    class TimerRunnableEvent(val sender: ProxyCommandSender, val template: Template) : BukkitProxyEvent()

}
