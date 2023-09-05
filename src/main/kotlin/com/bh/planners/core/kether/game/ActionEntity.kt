package com.bh.planners.core.kether.game

import com.bh.planners.api.common.SimpleTimeoutTask
import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.target
import com.bh.planners.core.kether.*
import com.bh.planners.core.kether.common.KetherHelper
import com.bh.planners.core.kether.common.KetherHelper.actionContainerOrOrigin
import com.bh.planners.core.kether.common.KetherHelper.containerOrElse
import com.bh.planners.core.kether.common.KetherHelper.containerOrOrigin
import com.bh.planners.core.kether.common.MultipleKetherParser
import com.bh.planners.core.kether.game.entity.*
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.util.Vector
import taboolib.module.kether.*
import java.util.*

object ActionEntity : MultipleKetherParser("entity") {

    val spawn = KetherHelper.simpleKetherParser<Target.Container> {
        it.group(
                text(),
                text(),
                long(),
                command("health", then = double()).option().defaultsTo(1.0),
                command("vector", then = bool()).option().defaultsTo(false),
                containerOrOrigin()
        ).apply(it) { type, name, tick, health, isVector, origin ->
            val entityType = EntityType.valueOf(type.uppercase().replace("-", "_"))
            now {
                val vector = if (isVector) {
                    bukkitPlayer()?.velocity ?: Vector(0, 0, 0)
                } else {
                    Vector(0, 0, 0)
                }
                val container = Target.Container()
                origin.forEachLocation {
                    val entity = createEntity(this, entityType)
                    // 设置实体名称
                    entity.bukkitLivingEntity?.customName = name
                    // 设置实体血量
                    if (health >= 1.0) {
                        entity.bukkitLivingEntity?.maxHealth = health
                        entity.bukkitLivingEntity?.health = health
                    }
                    entity.bukkitLivingEntity?.velocity = vector
                    // 注册到容器
                    container += entity
                }
                this.variables()["@select-entities"] = container
                this.variables()["@select-entities-cache"] = container.hashCode()
                // 注册延迟销毁
                SimpleTimeoutTask.createSimpleTask(tick, false) {
                    container.forEachLivingEntity { remove() }
                    this.variables().get<Int>("@selecte-entities-cache").ifPresent {
                        if (it == container.hashCode()) {
                            this.variables().remove("@select-entities")
                            this.variables().remove("@select-entities-cache")
                        }
                    }
                }
                container
            }
        }
    }

    val view = KetherHelper.simpleKetherParser<Unit> {
        it.group(float(), float(), containerOrElse { getSelectEntities() }).apply(it) { yaw, pitch, container ->
            now {
                container.forEachProxyEntity {
                    location.yaw = yaw
                    location.pitch = pitch
                }
            }
        }
    }

    val viewto = KetherHelper.simpleKetherParser<Unit> {
        it.group(actionContainerOrOrigin(), containerOrElse { getSelectEntities() }).apply(it) { target, container ->
            now {
                val t = target.firstBukkitLocation()!!
                container.forEachProxyEntity {
                    location.yaw = t.yaw
                    location.pitch = t.pitch
                }
            }
        }
    }

    val remove = KetherHelper.simpleKetherParser<Unit> {
        it.group(containerOrElse { getSelectEntities() }).apply(it) { container ->
            now { container.forEachProxyEntity {
                delete()
            } }
        }
    }

    val gravity = KetherHelper.simpleKetherParser<Unit> {
        it.group(bool(), containerOrElse { getSelectEntities() }).apply(it) { isGravity, container ->
            now { container.forEachLivingEntity { setGravity(isGravity) } }
        }
    }

    val main = KetherHelper.simpleKetherParser<Any?> {
        it.group(text(), containerOrElse { getSelectEntities() }).apply(it) { id, container ->
            val field = EntityField.valueOf(id.uppercase().replace("-", "_"))
            now { field.get(container.firstProxyEntity() ?: return@now null) }
        }
    }

    private fun createEntity(location: Location, type: EntityType): Target.Entity {
        return location.world!!.spawnEntity(location, type).target()
    }

    private fun ScriptFrame.getSelectEntities(): Target.Container {
        return rootVariables().get<Target.Container>("@selecte-entities").orElse(Target.Container())
    }

}