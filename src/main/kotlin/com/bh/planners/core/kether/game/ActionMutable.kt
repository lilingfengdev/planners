package com.bh.planners.core.kether.game

import com.bh.planners.core.kether.common.CombinationKetherParser
import com.bh.planners.core.kether.common.KetherHelper
import com.bh.planners.core.kether.nextOptionalParsedAction
import com.bh.planners.core.pojo.chant.Mutable
import taboolib.common.util.random
import taboolib.module.kether.KetherParser
import taboolib.module.kether.combinationParser
import taboolib.module.kether.scriptParser
import taboolib.module.kether.switch

/**
 * 创建模板可变文本
 * mutabletext message with fill <space: action("")> <step: action(10)>
 */
@CombinationKetherParser.Used
fun mutabletext() = KetherHelper.simpleKetherParser<String> {
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