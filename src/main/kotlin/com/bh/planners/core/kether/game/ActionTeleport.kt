package com.bh.planners.core.kether.game

import com.bh.planners.core.kether.*
import org.bukkit.Location
import org.bukkit.entity.LivingEntity
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionTeleport(
    val action: ParsedAction<*>,
    val selector: ParsedAction<*>?
) : ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        return frame.newFrame(action).run<Any>().thenAccept {
            if (selector != null) {
                frame.execEntity(selector) { execute(this, it) }
            } else {
                execute(frame.asPlayer() ?: return@thenAccept, it)
            }
        }


    }

    fun execute(entity: LivingEntity, it: Any) {

        if (it is LivingEntity) {
            entity.teleport(it)
        } else if (it is Location) {
            entity.teleport(it)
        } else if (it is String) {
            entity.teleport(it.toString().toLocation())
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
        @KetherParser(["teleport", "tp"])
        fun parser() = scriptParser {
            val parsedAction = it.next(ArgTypes.ACTION)
            ActionTeleport(parsedAction, it.selectorAction())
        }
    }

}