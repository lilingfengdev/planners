package com.bh.planners.core.selector.bukkit

import com.bh.planners.core.effect.Target.Companion.getLocation
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.selector.Selector
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import java.util.concurrent.CompletableFuture

/**
 * 选中世界内的实体
 * 世界:类型限制
 * @inWorld world PLAYER ZOMBIE
 * @inworld world PLAYER ZOMBIE
 * @iw world PLAYER ZOMBIE
 * @piw world PLAYER ZOMBIE
 */
object InWorld : Selector {

    override val names: Array<String>
        get() = arrayOf("inWorld", "inworld", "iw", "piw")

    // @inWorld world:PLAYER,ZOMBIE
    override fun check(data: Selector.Data): CompletableFuture<Void> {

        val worldName = data.read<String>(0, data.origin.getLocation()?.world?.name ?: error("InWorld no world args"))

        val types = data.values.subList(1, data.values.size)

        val world = Bukkit.getWorld(worldName)!!
        val targets =
            world.entities.filterIsInstance<LivingEntity>().filter { it.type.name in types }.map { it.toTarget() }
        data.container += targets
        return CompletableFuture.completedFuture(null)
    }

}