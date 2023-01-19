package com.bh.planners.core.timer.bukkit

import com.bh.planners.api.event.ISource
import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.consoleTarget
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.timer.*
import com.bh.planners.core.timer.TimerDrive.getTemplates
import org.bukkit.Bukkit
import taboolib.common.platform.Schedule
import taboolib.platform.type.BukkitProxyEvent

object TRunnable : AbstractTimer<TRunnable.TimerRunnableEvent>() {

    override val eventClazz: Class<TimerRunnableEvent>
        get() = TimerRunnableEvent::class.java

    var pointer = 0

    @Schedule(period = 1, async = true)
    fun run() {
        getTemplates(this).filter { hasNext(it) }.forEach {
            if (it.sender == "console") {
                TimerRunnableEvent(consoleTarget, it).call()
            } else if (it.sender == "player") {
                Bukkit.getOnlinePlayers().forEach { player ->
                    TimerRunnableEvent(player.toTarget(), it).call()
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

    override fun check(e: TimerRunnableEvent): Target? {
        return e.sender
    }

    override val name: String
        get() = "runnable"

    val Template.sender: String
        get() = root.getString("__option__.sender", "player")!!

    class TimerRunnableEvent(val sender: Target, val template: Template) : BukkitProxyEvent(), ISource {

        override fun id(): String {
            return template.id
        }

    }

}
