package com.bh.planners.core.feature.damageable

import java.util.concurrent.CompletableFuture

object DamageableDispatcher {

    fun invokeDamageable(context: Damageable) {
        val model = context.model
        val futureAttack = CompletableFuture<Void>()
        val futureDefend = CompletableFuture<Void>()

        runStreams(context,0,futureAttack,model.attackStreams)
        runStreams(context,0,futureAttack,model.defendStreams)
        futureAttack.thenAccept {
            futureDefend.thenAccept {
                // 处理完成
                // pre-action 一级处理

            }
        }

    }

    private fun runStreams(context: Damageable,index: Int,future: CompletableFuture<Void>,streams: List<DamageableModel.Stream>) {
        if (streams.isEmpty() || index >= streams.size) {
            future.complete(null)
            return
        }
        val stream = streams[index]
        DamageableScript.createScriptStream(context,stream).thenAccept {
            runStreams(context, index + 1, future, streams)
        }

    }

}