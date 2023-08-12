package com.bh.planners.core.kether

import com.bh.planners.api.ContextAPI
import com.bh.planners.api.script.ScriptLoader
import com.bh.planners.core.kether.common.CombinationKetherParser
import com.bh.planners.core.kether.common.containerOrEmpty
import com.bh.planners.core.kether.common.simpleKetherParser
import com.bh.planners.core.pojo.Skill
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

@CombinationKetherParser.Used
fun invoke() = simpleKetherParser<Void?>("invoke") {
    it.group(text(), command("using", then = anyList()),containerOrEmpty()).apply(it) { method, args , container -> ->
        now {
            if (skill().instance.script.mode == Skill.ActionMode.SIMPLE) return@now null
            container.forEach { target ->
                val session = ContextAPI.createSession(target, skill().instance)
                ScriptLoader.invokeFunction(session,method)
            }
            null
        }
    }
}