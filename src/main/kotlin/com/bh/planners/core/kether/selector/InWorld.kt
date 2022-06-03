package com.bh.planners.core.kether.selector

import com.bh.planners.core.kether.effect.Target
import com.bh.planners.core.kether.effect.Target.Companion.toTarget
import com.bh.planners.core.pojo.Session
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity

object InWorld : Selector {

    override val names: Array<String>
        get() = arrayOf("inWorld", "inworld", "iw", "piw")

    // -@inWorld world:PLAYER,ZOMBIE
    override fun check(target: Target?, args: String, session: Session, container: Target.Container) {

        val worldName = if (args.contains(":")) {
            args.split(":")[0]
        } else {
            val location = target as? Target.Location ?: return
            location.value.world!!.name
        }
        val types = args.replaceFirst("${worldName}:", "").split(",").map { it.uppercase() }

        val world = Bukkit.getWorld(worldName)!!
        val targets = world.entities.filterIsInstance<LivingEntity>()
            .filter { it.type.name in types }
            .map { it.toTarget() }
        container.addAll(targets)
    }

}