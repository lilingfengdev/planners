package com.bh.planners.core.kether.game.entity

import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import java.util.*
import java.util.concurrent.CompletableFuture

class ActionOfEntity(val action: ParsedAction<*>) : ScriptAction<LivingEntity>() {
    override fun run(frame: ScriptFrame): CompletableFuture<LivingEntity> {
        return frame.newFrame(action).run<Any>().thenApply {
            Bukkit.getEntity(UUID.fromString(it.toString())) as LivingEntity
        }
    }

}