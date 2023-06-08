package com.bh.planners.core.kether.game

import com.bh.planners.core.effect.Target
import com.bh.planners.core.kether.bukkitPlayer
import com.bh.planners.core.kether.execEntity
import com.bh.planners.core.kether.nextSelectorOrNull
import com.bh.planners.core.kether.toLocation
import org.bukkit.Location
import org.bukkit.entity.Entity
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionTeleport(val action: ParsedAction<*>, val selector: ParsedAction<*>?) : ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        return frame.run(action).thenAccept {
            if (selector != null) {
                frame.execEntity(selector) {
                    execute(this, it!!)
                }
            } else {
                execute(frame.bukkitPlayer() ?: return@thenAccept, it!!)
            }
        }
    }

    fun execute(entity: Entity, it: Any) {
        when (it) {
            is Entity -> {
                entity.teleport(it)
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
            val parsedAction = it.nextParsedAction()
            ActionTeleport(parsedAction, it.nextSelectorOrNull())
        }
    }

}