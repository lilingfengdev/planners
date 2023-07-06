package com.bh.planners.core.kether.game.entity

import com.bh.planners.api.common.SimpleTimeoutTask
import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.getEntity
import com.bh.planners.core.kether.createContainer
import com.bh.planners.core.kether.origin
import com.bh.planners.core.kether.runAny
import com.bh.planners.core.kether.senderPlannerProfile
import io.lumine.xikage.mythicmobs.MythicMobs
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.util.Vector
import taboolib.common.util.sync
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
    val vector: ParsedAction<*>,
    val selector: ParsedAction<*>?,
) : ScriptAction<List<Entity>>() {


    fun spawn(
        entityType: EntityType,
        locations: List<Location>,
        name: String,
        health: Double,
        tick: Long,
        vector: List<Vector>
    ): CompletableFuture<List<Entity>> {
        val future = CompletableFuture<List<Entity>>()
        if (name.startsWith("mm:")) {
            spawn(name.removePrefix("mm:"), locations).thenAccept {
                it.forEach { register(it, tick) }
                future.complete(it)
            }
        } else {
            spawn(entityType, locations, vector).thenAccept {
                it.forEach { register(it, name, health, tick) }
                future.complete(it)
            }
        }
        return future
    }

    fun spawn(mob: String, locations: List<Location>): CompletableFuture<List<Entity>> {
        val future = CompletableFuture<List<Entity>>()
        if (Bukkit.isPrimaryThread()) {
            future.complete(locations.map { MythicMobs.inst().mobManager.spawnMob(mob, it, 1.0).entity.bukkitEntity })
        } else {
            sync {
                future.complete(locations.map { MythicMobs.inst().mobManager.spawnMob(mob, it, 1.0).entity.bukkitEntity })
            }
        }
        return future
    }

    fun spawn(entityType: EntityType, locations: List<Location>, vector: List<Vector>): CompletableFuture<List<Entity>> {
        val future = CompletableFuture<List<Entity>>()
        if (Bukkit.isPrimaryThread()) {
            future.complete(locations.mapIndexed { index, location -> spawn(entityType, location, vector[index]) })
        } else {
            sync {
                future.complete(locations.mapIndexed { index, location -> spawn(entityType, location, vector[index]) })
            }
        }
        return future
    }

    fun spawn(entityType: EntityType, location: Location, vector: Vector): Entity {
        return if (entityType == EntityType.ARMOR_STAND) {
            location.world!!.spawnEntity(location, entityType).apply {
                this.isInvulnerable = true
                velocity = vector
            }
        } else {
            location.world!!.spawnEntity(location, entityType)
        }
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

    fun register(entity: Entity, tick: Long): UUID {
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
                        frame.runAny(vector) {
                            val vec = Coerce.toBoolean(this)
                            if (selector != null) {
                                frame.createContainer(selector).thenAccept {
                                    val locations = it.filterIsInstance<Target.Location>().map { it.value }
                                    val vector =
                                        if (vec) {
                                            it.filterIsInstance<Target.Location>().map { frame.senderPlannerProfile()?.player?.velocity ?: Vector(0, 0, 0) }
                                        } else {
                                            it.filterIsInstance<Target.Location>().map { Vector(0, 0, 0) }
                                        }
                                    spawn(entityType, locations, name, health, tick, vector).thenAccept {
                                        future.complete(it)
                                    }
                                }
                            } else {
                                val vector =
                                    if (vec) {
                                        frame.senderPlannerProfile()?.player?.velocity ?: Vector(0, 0, 0)
                                    } else {
                                        Vector(0, 0, 0)
                                    }
                                spawn(
                                    entityType,
                                    listOf(frame.origin().value),
                                    name,
                                    health,
                                    tick,
                                    listOf(vector)
                                ).thenAccept {
                                    future.complete(it)
                                }
                            }
                        }

                    }
                }

            }
        }

        return future
    }

}
