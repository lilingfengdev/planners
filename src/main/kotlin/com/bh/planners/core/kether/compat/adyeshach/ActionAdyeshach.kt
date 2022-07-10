package com.bh.planners.core.kether.compat.adyeshach

import com.bh.planners.api.common.SimpleTimeoutTask
import com.bh.planners.core.effect.Target
import com.bh.planners.core.kether.*
import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.*
import org.bukkit.entity.*
import taboolib.common.platform.function.submit
import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.library.kether.actions.LiteralAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionAdyeshach {

    class ActionEntitySpawn(
        val type: ParsedAction<*>,
        val name: ParsedAction<*>,
        val timeout: ParsedAction<*>,
        val selector: ParsedAction<*>?
    ) : ScriptAction<List<Entity>>() {


        fun spawn(
            entityType: EntityTypes, locations: List<Location>, name: String, tick: Long
        ): CompletableFuture<List<EntityInstance>> {
            val future = CompletableFuture<List<EntityInstance>>()
            spawn(entityType, locations).thenAccept {
                it.forEach { register(it, name, tick) }
                future.complete(it)
            }
            return future
        }

        fun spawn(entityType: EntityTypes, locations: List<Location>): CompletableFuture<List<EntityInstance>> {
            val future = CompletableFuture<List<EntityInstance>>()
            if (Bukkit.isPrimaryThread()) {
                future.complete(locations.map { spawn(entityType, it) })
            } else {
                submit(async = false) {
                    future.complete(locations.map { spawn(entityType, it) })
                }
            }
            return future
        }

        fun spawn(entityType: EntityTypes, location: Location): EntityInstance {
            return AdyeshachAPI.getEntityManagerPublicTemporary().create(entityType, location)
        }


        fun register(entity: EntityInstance, name: String, tick: Long): String {
            entity.setCustomName(name)
            // 注册销毁任务
            SimpleTimeoutTask.createSimpleTask(tick, true) {
                entity.destroy()
            }
            return "ady:${entity.uniqueId}"
        }

        override fun run(frame: ScriptFrame): CompletableFuture<List<Entity>> {
            val future = CompletableFuture<List<Entity>>()
            frame.runAny(type) {
                val entityType = EntityTypes.valueOf(toString())
                frame.runAny(name) {
                    val name = toString()
                    frame.runAny(timeout) {
                        val tick = Coerce.toLong(this)
                        if (selector != null) {
                            frame.createTargets(selector).thenAccept {
                                catchRunning {
                                    val locations = it.targets.filterIsInstance<Target.Location>().map { it.value }
                                    spawn(entityType, locations, name, tick).thenAccept {
                                        future.complete(it.map { AdyeshachEntity(it) })
                                    }
                                }
                            }
                        } else {
                            spawn(entityType, listOf(frame.toOriginLocation()!!.value), name, tick).thenAccept {
                                future.complete(it.map { AdyeshachEntity(it) })
                            }
                        }

                    }

                }
            }

            return future
        }

    }

    class AdyeshachEntityFollow(val owner: ParsedAction<*>, val selector: ParsedAction<*>,val option : ParsedAction<*>) : ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {

            return frame.createTargets(owner).thenAccept {
                val entityTarget = it.firstLivingEntityTarget() ?: return@thenAccept
                frame.newFrame(option).run<Any>().thenAccept {
                    val option = it.toString()
                    frame.execEntity(selector) {
                        if (this is AdyeshachEntity) {
                            EntityFollow.select(entityTarget, this.entity, option)
                        }
                    }
                }

            }
        }

    }

    companion object {


        /**
         * adyeshach spawn type name tick
         * adyeshach follow <option: action> [owner:first] [selector:entity]
         */
        @KetherParser(["adyeshach", "ady"], namespace = NAMESPACE)
        fun parser() = scriptParser {
            it.switch {
                case("spawn") {
                    ActionEntitySpawn(
                        it.next(ArgTypes.ACTION),
                        it.next(ArgTypes.ACTION),
                        it.next(ArgTypes.ACTION),
                        it.selectorAction()
                    )
                }
                case("follow") {
                    val option = try {
                        it.mark()
                        it.expects("option","params")
                        it.next(ArgTypes.ACTION)
                    } catch (_ : Throwable) {
                        it.reset()
                        ParsedAction(LiteralAction<Long>("EMPTY"))
                    }
                    AdyeshachEntityFollow(it.next(ArgTypes.ACTION), it.next(ArgTypes.ACTION),option)
                }
            }

        }

    }


}