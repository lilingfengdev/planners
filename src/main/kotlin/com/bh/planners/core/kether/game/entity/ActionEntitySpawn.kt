package com.bh.planners.core.kether.game.entity

import com.bh.planners.api.common.SimpleTimeoutTask
import com.bh.planners.core.effect.Target
import com.bh.planners.core.kether.createContainer
import com.bh.planners.core.kether.runAny
import com.bh.planners.core.kether.toOriginLocation
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import taboolib.common.platform.function.submit
import taboolib.common5.Coerce
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import java.util.*
import java.util.concurrent.CompletableFuture

class ActionEntitySpawn(
    val type: ParsedAction<*>,
    val name: ParsedAction<*>,
    val health: ParsedAction<*>,
    val tick: ParsedAction<*>,
    val selector: ParsedAction<*>?
) : ScriptAction<List<Entity>>() {


    fun spawn(
        entityType: EntityType, locations: List<Location>, name: String, health: Double, tick: Long
    ): CompletableFuture<List<Entity>> {
        val future = CompletableFuture<List<Entity>>()
        spawn(entityType, locations).thenAccept {
            it.forEach { register(it, name, health, tick) }
            future.complete(it)
        }
        return future
    }

    fun spawn(entityType: EntityType, locations: List<Location>): CompletableFuture<List<Entity>> {
        val future = CompletableFuture<List<Entity>>()
        if (Bukkit.isPrimaryThread()) {
            future.complete(locations.map { spawn(entityType, it) })
        } else {
            submit(async = false) {
                future.complete(locations.map { spawn(entityType, it) })
            }
        }
        return future
    }

    fun spawn(entityType: EntityType, location: Location): Entity {
        return location.world!!.spawnEntity(location, entityType)
    }


    fun register(entity: Entity, name: String, health: Double, tick: Long): UUID {
        entity.customName = name
        if (entity is LivingEntity) {
            entity.maxHealth = health
            entity.health = health
        }
        // 注册销毁任务
        SimpleTimeoutTask.createSimpleTask(tick, false) {
            entity.remove()
        }
        return entity.uniqueId
    }

    override fun run(frame: ScriptFrame): CompletableFuture<List<Entity>> {
        val future = CompletableFuture<List<Entity>>()
        frame.runAny(type) {
            val entityType = EntityType.valueOf(toString())
            frame.runAny(name) {
                val name = toString()
                frame.runAny(health) {
                    val health = Coerce.toDouble(this)
                    frame.runAny(tick) {
                        val tick = Coerce.toLong(this)
                        if (selector != null) {
                            frame.createContainer(selector).thenAccept {
                                val locations = it.targets.filterIsInstance<Target.Location>().map { it.value }
                                spawn(entityType, locations, name, health, tick).thenAccept {
                                    future.complete(it)
                                }
                            }
                        } else {
                            spawn(
                                entityType, listOf(frame.toOriginLocation()!!.value), name, health, tick
                            ).thenAccept {
                                future.complete(it)
                            }
                        }

                    }
                }

            }
        }

        return future
    }

}
