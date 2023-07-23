package com.bh.planners.core.kether.game

import com.bh.planners.api.entity.ProxyEntity
import com.bh.planners.core.effect.Target
import com.bh.planners.core.kether.containerOrSender
import com.bh.planners.core.kether.nextSelectorOrNull
import com.bh.planners.core.kether.toLocation
import org.bukkit.Location
import org.bukkit.entity.Entity
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionTeleport(val action: ParsedAction<*>, val selector: ParsedAction<*>?) : ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        return frame.run(action).thenAccept { action ->
            frame.containerOrSender(selector).thenAccept {
                it.forEachProxyEntity {
                    execute(this, action!!)
                }
            }
        }
    }

    fun execute(entity: ProxyEntity, it: Any) {
        when (it) {
            is Entity -> {
                entity.teleport(it.location)
            }

            is Location -> {
                entity.teleport(it)
            }

            is Target.Container -> {
                entity.teleport(it.firstLocation() ?: return)
            }

            is Target.Location -> {
                entity.teleport(it.value)
            }

            is String -> {
                entity.teleport(it.toString().toLocation())
            }
        }
    }

    companion object {

        /**
         *
         * 传送实体到指定目标 传入the参数则给选中实体目标全部传送 不传则默认执行者
         * entity test: teleport entity of &@entityUniqueId
         * location test: teleport location
         * location test: teleport "world,0,0,0"
         *
         * teleport [location&entity] <the "selector">
         */
        @KetherParser(["teleport", "tp"], shared = true)
        fun parser() = scriptParser {
            ActionTeleport(it.nextParsedAction(), it.nextSelectorOrNull())
        }
    }

}