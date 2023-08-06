package com.bh.planners.core.kether.game

import com.bh.planners.core.kether.nextArgumentAction
import com.bh.planners.core.pojo.chant.Mutable
import taboolib.common.util.random
import taboolib.module.kether.KetherParser
import taboolib.module.kether.combinationParser
import taboolib.module.kether.scriptParser
import taboolib.module.kether.switch

object ActionMutable {


    // mutabletext message with fill <space: ""> <step: 10>
    @KetherParser(["mutabletext"])
    fun parser() = combinationParser {
        it.group(
            text(),
            text(),
            text(),
            command("space", then = text()).option().defaultsTo(""),
            command("step", then = int()).option().defaultsTo(10),
            command("with", then = double()).option().defaultsTo(random(0.0, 1.0))
        ).apply(it) { message, connect, fill, space, step, with ->
            now { Mutable.Text(message, connect, fill, space, step).build(with) }
        }
    }

}