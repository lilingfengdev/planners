package com.bh.planners.core.kether.effect.selector

import com.bh.planners.core.kether.effect.Target
import com.bh.planners.core.kether.effect.Target.Companion.toTarget
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import java.util.*

object EntityId : Selector {
    override val names: Array<String>
        get() = arrayOf("entity", "ei", "entityId")

    override fun check(args: String, sender: Player, container: Target.Container) {
        args.split(",").forEach {
            val entity = Bukkit.getEntity(UUID.fromString(it)) as? LivingEntity ?: return@forEach
            container.add(entity.toTarget())
        }
    }
}
