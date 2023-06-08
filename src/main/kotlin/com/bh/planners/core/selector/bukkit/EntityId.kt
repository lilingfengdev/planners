package com.bh.planners.core.selector.bukkit

import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.selector.Selector
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * 根据entity id来选中特殊实体
 * @entity 230
 */
object EntityId : Selector {
    override val names: Array<String>
        get() = arrayOf("entity", "ei", "entityId")

    override fun check(data: Selector.Data): CompletableFuture<Void> {
        data.values.forEach {
            val entity = Bukkit.getEntity(UUID.fromString(it)) as? LivingEntity ?: return@forEach
            data.container += (entity.toTarget())
        }
        return CompletableFuture.completedFuture(null)
    }
}
