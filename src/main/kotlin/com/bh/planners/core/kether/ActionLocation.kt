package com.bh.planners.core.kether

import com.bh.planners.core.kether.common.CombinationKetherParser
import com.bh.planners.core.kether.common.SimpleKetherParser
import taboolib.common.platform.function.platformLocation
import taboolib.common.util.Location
import taboolib.module.kether.*

/**
 * @author IzzelAliz
 */
@CombinationKetherParser.Used
object ActionLocation : SimpleKetherParser("location", "loc") {
    override fun run(): ScriptActionParser<Any?> {
        return combinationParser {
            it.group(
                    text(),
                    double(),
                    double(),
                    double(),
                    command("and", then = float().and(float())).option().defaultsTo(0f to 0f)
            ).apply(it) { world, x, y, z, (yaw, pitch) ->
                now { platformLocation<Any>(Location(world, x, y, z, yaw, pitch)) }
            }
        }
    }
}