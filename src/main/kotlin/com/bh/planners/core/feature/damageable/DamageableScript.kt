package com.bh.planners.core.feature.damageable

import com.bh.planners.api.ContextAPI
import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.isPlayer
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.kether.rootVariables
import com.bh.planners.core.pojo.Context
import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.kether.KetherShell
import taboolib.module.kether.ScriptContext
import taboolib.module.kether.runKether
import java.util.concurrent.CompletableFuture

object DamageableScript {

    const val NAMESPACE = "damageable"
    val NAMESPACES = listOf(NAMESPACE)

    fun createScriptStream(context: Damageable,stream: DamageableModel.Stream): CompletableFuture<DamageableMeta> {
        val meta = DamageableMeta(context, stream)
        if (stream.data != null) {
            return invokeScript(stream.data,meta.sender.toTarget()) {
                this.rootFrame().setDamageable(context)
                this.rootFrame().setDamageableMeta(meta)
            }.thenApply {
                meta.data = it
                meta
            }
        }
        return CompletableFuture.completedFuture(meta)
    }

    fun invokeMetaScript(script: String,context: Damageable,meta: DamageableMeta,block: ScriptContext.() -> Unit = {}): CompletableFuture<Any?> {
        return invokeScript(script,meta.sender.toTarget()) {
            this.rootFrame().setDamageable(context)
            this.rootFrame().setDamageableMeta(meta)
            this.rootFrame().variables()["data"] = meta.data
            this.rootFrame().variables()["@Context"] = object : Context(meta.sender.toTarget()) {  }
            block(this)
        }.thenApply {
            // 结束流
            if (meta.cancelStream) {
                context.metaCancel = meta
            }
            it
        }.exceptionally { it.printStackTrace() }
    }

    fun invokeScript(script: String, sender: Target.Entity,block: ScriptContext.() -> Unit = {}): CompletableFuture<Any?> {
        return runKether {
            KetherShell.eval(script, namespace = listOf(NAMESPACE,"Planners")) {
                val entity = sender.bukkitLivingEntity!!
                if (entity is Player) {
                    this.sender = adaptPlayer(entity)
                }
                block()
            }
        } ?: CompletableFuture.completedFuture(null)
    }

}