package com.bh.planners.core.kether.selector

import com.bh.planners.core.pojo.Context
import com.bh.planners.core.skill.effect.Target
import com.bh.planners.core.skill.effect.Target.Companion.toTarget
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import java.util.concurrent.CompletableFuture

/**
 * 选中世界内的实体
 * 世界:类型限制
 * -@inWorld world:PLAYER,ZOMBIE
 * -@inworld world:PLAYER,ZOMBIE
 * -@iw world:PLAYER,ZOMBIE
 * -@piw world:PLAYER,ZOMBIE
 */
object InWorld : Selector {

    override val names: Array<String>
        get() = arrayOf("inWorld", "inworld", "iw", "piw")

    // -@inWorld world:PLAYER,ZOMBIE
    override fun check(name: String, target: Target?, args: String, context: Context, container: Target.Container): CompletableFuture<Void> {

        val worldName = if (args.contains(":")) {
            args.split(":")[0]
        } else {
            val location = target as? Target.Location ?: return CompletableFuture.completedFuture(null)
            location.value.world!!.name
        }
        val types = args.replaceFirst("${worldName}:", "").split(",").map { it.uppercase() }

        val world = Bukkit.getWorld(worldName)!!
        val targets = world.entities.filterIsInstance<LivingEntity>()
            .filter { it.type.name in types }
            .map { it.toTarget() }
        container.addAll(targets)
        return CompletableFuture.completedFuture(null)
    }

}