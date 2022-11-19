package com.bh.planners.core.timer.impl

import com.bh.planners.api.event.ISource
import com.bh.planners.core.timer.*
import com.bh.planners.core.timer.TimerDrive.getTemplates
import org.bukkit.Bukkit
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.Schedule
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.console
import taboolib.common.platform.function.info
import taboolib.platform.type.BukkitProxyEvent
import java.util.Collections

object TimerRunnable : AbstractTimer<TimerRunnable.TimerRunnableEvent>() {

    override val eventClazz: Class<TimerRunnableEvent>
        get() = TimerRunnableEvent::class.java

    var pointer = 0

    @Schedule(period = 1, async = true)
    fun run() {
        getTemplates(this).filter { hasNext(it) }.forEach {
            if (it.sender == "console") {
                TimerRunnableEvent(console(), it).call()
            } else if (it.sender == "player") {
                Bukkit.getOnlinePlayers().forEach { player ->
                    TimerRunnableEvent(adaptPlayer(player), it).call()
                }
            }

        }
        pointer++
    }

    fun hasNext(template: Template): Boolean {
        return pointer % template.period() == 0L
    }


    private fun Template.period(): Long {
        return root.getLong("__option__.period", 1000L) / 50
    }

    override fun check(e: TimerRunnableEvent): ProxyCommandSender? {
        return e.sender
    }

    override val name: String
        get() = "runnable"

    val Template.sender: String
        get() = root.getString("__option__.sender", "player")!!

    class TimerRunnableEvent(val sender: ProxyCommandSender, val template: Template) : BukkitProxyEvent(), ISource {

        override fun id(): String {
            return template.id
        }

    }

}
