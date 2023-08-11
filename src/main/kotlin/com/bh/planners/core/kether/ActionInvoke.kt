package com.bh.planners.core.kether

import com.bh.planners.api.ContextAPI
import com.bh.planners.api.script.ScriptLoader
import com.bh.planners.core.kether.common.simpleKetherParser
import com.bh.planners.core.pojo.Skill
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

fun invoke() = simpleKetherParser<String>("invoke") {
    it.group(text(), command("using", then = anyList())).apply(it) { method, args ->

    }
}