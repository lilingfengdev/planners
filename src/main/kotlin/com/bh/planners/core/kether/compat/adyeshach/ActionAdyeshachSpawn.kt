package com.bh.planners.core.kether.compat.adyeshach

import com.bh.planners.api.common.SimpleTimeoutTask
import com.bh.planners.api.entity.ProxyAdyeshachEntity
import com.bh.planners.api.entity.ProxyEntity
import com.bh.planners.core.effect.Target
import com.bh.planners.core.kether.*
import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.*
import org.bukkit.entity.*
import taboolib.common.platform.function.submit
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionAdyeshachSpawn : ScriptAction<List<ProxyEntity>>() {

    lateinit var type: ParsedAction<*>
    lateinit var name: ParsedAction<*>
    lateinit var timeout: ParsedAction<*>
    var selector: ParsedAction<*>? = null

    fun spawn(entityType: EntityTypes, locations: List<Location>, name: String, tick: Long): CompletableFuture<List<ProxyEntity>> {
        val future = CompletableFuture<List<ProxyEntity>>()
        spawn(entityType, locations).thenAccept {
            it.forEach { register(it, name, tick) }
            future.complete(it)
        }
        return future
    }

    fun spawn(entityType: EntityTypes, locations: List<Location>): CompletableFuture<List<ProxyAdyeshachEntity>> {
        val future = CompletableFuture<List<ProxyAdyeshachEntity>>()
        if (Bukkit.isPrimaryThread()) {
            future.complete(locations.map { spawn(entityType, it) })
        } else {
            submit(async = false) {
                future.complete(locations.map { spawn(entityType, it) })
            }
        }
        return future
    }

    fun spawn(entityType: EntityTypes, location: Location): ProxyAdyeshachEntity {
        return ProxyAdyeshachEntity(AdyeshachAPI.getEntityManagerPublicTemporary().create(entityType, location))
    }


    fun register(entity: ProxyAdyeshachEntity, name: String, tick: Long): String {
        entity.customName = name
        // 注册销毁任务
        SimpleTimeoutTask.createSimpleTask(tick, true) {
            if (!entity.isDeleted) {
                entity.instance.delete()
            }
        }
        return "ady:${entity.uniqueId}"
    }

    override fun run(frame: ScriptFrame): CompletableFuture<List<ProxyEntity>> {
        val future = CompletableFuture<List<ProxyEntity>>()
        frame.runTransfer<EntityTypes>(type).thenAccept { type ->
            frame.runTransfer<String>(name).thenAccept { name ->
                frame.runTransfer<Long>(timeout).thenAccept { timeout ->
                    if (selector != null) {
                        frame.createContainer(selector!!).thenAccept {
                            val locations = it.filterIsInstance<Target.Location>().map { it.value }
                            spawn(type, locations, name, timeout).thenAccept {
                                future.complete(it)
                            }
                        }
                    } else {
                        spawn(type, listOf(frame.origin().value), name, timeout).thenAccept {
                            future.complete(it)
                        }
                    }
                }
            }
        }

        return future
    }

}