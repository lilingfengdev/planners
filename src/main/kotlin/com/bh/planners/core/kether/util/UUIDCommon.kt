package com.bh.planners.core.kether.util

import com.bh.planners.core.kether.NAMESPACE
import taboolib.module.kether.KetherParser
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.scriptParser
import java.util.*
import java.util.concurrent.CompletableFuture


class UUIDCommon {

    class Random : ScriptAction<String>() {
        override fun run(frame: ScriptFrame): CompletableFuture<String> {
            return CompletableFuture.completedFuture(UUID.randomUUID().toString())
        }
    }

    companion object {

        @KetherParser(["uuid"], namespace = NAMESPACE)
        fun parser2() = scriptParser { Random() }
    }

}
