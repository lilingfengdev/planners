package com.bh.planners.core.kether.effect.selector

import com.bh.planners.core.kether.effect.Target
import com.bh.planners.core.kether.effect.Target.Companion.toTarget
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object PlayerInWorld : Selector{

    override val names: Array<String>
        get() = arrayOf("PlayerInWorld" , "piw")

    override fun check(args: String, sender: Player, container: Target.Container) {
        val worldName = args.ifEmpty { sender.world.name }
        val world = Bukkit.getWorld(worldName)!!
        val targets = world.entities
            .filterIsInstance<Player>()
            .map { it.toTarget() }
        container.addAll(targets)
    }

}