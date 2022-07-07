package com.bh.planners.core.kether

import com.bh.planners.api.EntityAPI
import com.bh.planners.core.skill.effect.Target
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import taboolib.common.platform.function.submit
import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import taboolib.module.nms.spawnEntity
import java.util.*
import java.util.concurrent.CompletableFuture

class ActionEntity {

    class OfEntity(val action: ParsedAction<*>) : ScriptAction<LivingEntity>() {
        override fun run(frame: ScriptFrame): CompletableFuture<LivingEntity> {
            return frame.newFrame(action).run<Any>().thenApply {
                Bukkit.getEntity(UUID.fromString(it.toString())) as LivingEntity
            }
        }

    }

    class LocationGet(val action: ParsedAction<*>) : ScriptAction<Location>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Location> {
            return frame.newFrame(action).run<Any>().thenApply {
                (Bukkit.getEntity(UUID.fromString(it.toString())) as LivingEntity).location
            }
        }
    }

    class ActionEntitySpawn(
        val type: ParsedAction<*>, val name: ParsedAction<*>, val health: ParsedAction<*>, val timeout: ParsedAction<*>,
        val selector: ParsedAction<*>?
    ) : ScriptAction<List<Entity>>() {


        fun spawn(
            entityType: EntityType,
            locations: List<Location>,
            name: String,
            health: Double,
            timeout: Long
        ): CompletableFuture<List<Entity>> {
            val future = CompletableFuture<List<Entity>>()
            spawn(entityType, locations).thenAccept {
                it.forEach { register(it, name, health, timeout) }
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


        fun register(entity: Entity, name: String, health: Double, timeout: Long): UUID {
            entity.customName = name
            if (entity is LivingEntity) {
                entity.maxHealth = health
                entity.health = health
            }
            EntityAPI.register(entity, timeout)
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
                        frame.runAny(timeout) {
                            val timeout = Coerce.toLong(this)
                            if (selector != null) {
                                frame.createTargets(selector).thenAccept {
                                    val locations = it.targets.filterIsInstance<Target.Location>().map { it.value }
                                    spawn(entityType, locations, name, health, timeout).thenAccept {
                                        future.complete(it)
                                    }
                                }
                            } else {
                                spawn(entityType, listOf(frame.toOriginLocation()!!.value), name, health, timeout).thenAccept {
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


    companion object {

        /**
         * entity of [uuid: action]
         * entity loc [entity : action]
         * entity spawn type name health timeout 返回 [ UUID ]
         */
        @KetherParser(["entity"])
        fun parser() = scriptParser {
            it.switch {
                case("of") {
                    OfEntity(it.next(ArgTypes.ACTION))
                }
                case("loc", "location") {
                    LocationGet(it.next(ArgTypes.ACTION))
                }
                case("spawn") {
                    ActionEntitySpawn(
                        it.next(ArgTypes.ACTION),
                        it.next(ArgTypes.ACTION),
                        it.next(ArgTypes.ACTION),
                        it.next(ArgTypes.ACTION),
                        it.selectorAction()
                    )
                }
            }

        }

    }

}