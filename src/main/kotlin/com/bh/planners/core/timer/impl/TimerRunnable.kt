package com.bh.planners.core.timer.impl

import com.bh.planners.core.timer.*
import com.bh.planners.core.timer.TimerDrive.getTemplates
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.platform.Schedule
import taboolib.platform.type.BukkitProxyEvent

object TimerRunnable : AbstractTimer<TimerRunnable.TimerRunnableEvent>() {

    override val eventClazz: Class<TimerRunnableEvent>
        get() = TimerRunnableEvent::class.java

    private val cache = mutableMapOf<String, Long>()

    @Schedule(period = 1, async = true)
    fun run() {
        getTemplates(this).filter { isClosed(it) }.forEach {
            mark(it)
            Bukkit.getOnlinePlayers().forEach { player ->
                TimerRunnableEvent(player, it).call()
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

    override fun check(e: TimerRunnableEvent): Player {
        return e.player
    }

    override val name: String
        get() = "runnable"

    class TimerRunnableEvent(val player: Player, val template: Template) : BukkitProxyEvent()

}
