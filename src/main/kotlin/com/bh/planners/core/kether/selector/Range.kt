package com.bh.planners.core.kether.selector

import com.bh.planners.core.kether.effect.Target
import com.bh.planners.core.kether.effect.Target.Companion.toTarget
import com.bh.planners.core.pojo.Session
import org.bukkit.entity.LivingEntity
import taboolib.common5.Coerce

/**
 * 选中根据原点来定义的范围实体
 * -@range 10
 * -@range 5,5,5
 */
object Range : Selector {

    override val names: Array<String>
        get() = arrayOf("range", "r")

    override fun check(name: String, target: Target?, args: String, session: Session, container: Target.Container) {

        val ranges = if (args.contains(",")) args.split(",") else listOf(args, args, args)
        val split = ranges.map { Coerce.toDouble(it) }

        val location = target as? Target.Location ?: return

        val targets = location.value.world!!
            .getNearbyEntities(location.value, split[0], split[1], split[2])
            .filterIsInstance<LivingEntity>()
            .map { it.toTarget() }
        container.addAll(targets)
    }
}
