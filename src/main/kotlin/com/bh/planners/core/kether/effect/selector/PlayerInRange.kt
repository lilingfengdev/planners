package com.bh.planners.core.kether.effect.selector

import com.bh.planners.core.kether.effect.Target
import com.bh.planners.core.kether.effect.Target.Companion.toTarget
import org.bukkit.entity.Player
import taboolib.common5.Coerce

object PlayerInRange : Selector{

    override val names: Array<String>
        get() = arrayOf("playerInRange" , "pir")

    override fun check(args: String, sender: Player, container: Target.Container) {
        val split = args.split(",").map { Coerce.toDouble(it) }
        val targets = sender.location.world!!
            .getNearbyEntities(sender.location, split[0], split[1], split[2])
            .filterIsInstance<Player>()
            .map { it.toTarget() }
        container.addAll(targets)
    }

}