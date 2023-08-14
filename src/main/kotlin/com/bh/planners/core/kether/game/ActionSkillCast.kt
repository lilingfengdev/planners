package com.bh.planners.core.kether.game

import com.bh.planners.api.ContextAPI
import com.bh.planners.api.PlannersAPI
import com.bh.planners.core.kether.NAMESPACE
import com.bh.planners.core.kether.bukkitPlayer
import com.bh.planners.core.kether.common.CombinationKetherParser
import com.bh.planners.core.kether.common.KetherHelper
import com.bh.planners.core.kether.common.KetherHelper.containerOrSender
import com.bh.planners.core.kether.execPlayer
import com.bh.planners.core.kether.nextSelectorOrNull
import taboolib.common5.Coerce
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.KetherParser
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture

@CombinationKetherParser.Used
fun trycast() = KetherHelper.simpleKetherParser<Unit>("cast","try-cast","cast-try`") {
    it.group(text(),containerOrSender()).apply(it) { skill,container ->
        now { container.forEachPlayer { PlannersAPI.cast(this,skill) } }
    }
}

@CombinationKetherParser.Used
fun directcast() = KetherHelper.simpleKetherParser<Unit>("cast") {
    it.group(text(), command("level", then = int()).option().defaultsTo(1),containerOrSender()).apply(it) { skill,level, container ->
        now { container.forEachPlayer { PlannersAPI.directCast(this,skill,level) } }
    }
}