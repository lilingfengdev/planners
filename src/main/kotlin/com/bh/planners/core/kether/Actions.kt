package com.bh.planners.core.kether

import com.bh.planners.core.kether.common.CombinationKetherParser
import com.bh.planners.core.kether.common.KetherHelper
import taboolib.common.platform.function.isPrimaryThread
import taboolib.common.platform.function.submit
import taboolib.module.kether.ScriptService
import taboolib.module.kether.script
import java.util.UUID
import java.util.concurrent.CompletableFuture

@CombinationKetherParser.Used
fun uuid() = KetherHelper.simpleKetherNow { UUID.randomUUID() }

@CombinationKetherParser.Used
fun cos() = KetherHelper.simpleKetherParser<Double> {
    it.group(double()).apply(it) { value ->
        now { kotlin.math.cos(value) }
    }
}

@CombinationKetherParser.Used
fun sin() = KetherHelper.simpleKetherParser<Double> {
    it.group(double()).apply(it) { value ->
        now { kotlin.math.sin(value) }
    }
}
@CombinationKetherParser.Used
fun radians() = KetherHelper.simpleKetherParser<Double> {
    it.group(double()).apply(it) { value ->
        now { Math.toRadians(value) }
    }
}

@CombinationKetherParser.Used
fun pow() = KetherHelper.simpleKetherParser<Double> {
    it.group(double(), double()).apply(it) { value,t ->
        now { Math.pow(value,t) }
    }
}

@CombinationKetherParser.Used
fun tan() = KetherHelper.simpleKetherParser<Double> {
    it.group(double()).apply(it) { value ->
        now { kotlin.math.tan(value) }
    }
}

@CombinationKetherParser.Used
fun atan() = KetherHelper.simpleKetherParser<Double> {
    it.group(double()).apply(it) { value ->
        now { kotlin.math.atan(value) }
    }
}

@CombinationKetherParser.Used
fun abs() = KetherHelper.simpleKetherParser<Double> {
    it.group(double()).apply(it) { value ->
        now { kotlin.math.abs(value) }
    }
}

@CombinationKetherParser.Used
fun sleep() = KetherHelper.simpleKetherParser<Void>("wait", "delay", "sleep") {
    it.group(long()).apply(it) { ticks ->
        future {
            val future = CompletableFuture<Void>()
            val task = submit(delay = ticks, async = !isPrimaryThread) {
                // 如果玩家在等待过程中离线则终止脚本
                if (script().sender?.isOnline() == false) {
                    ScriptService.terminateQuest(script())
                    return@submit
                }
                future.complete(null)
            }
            addClosable(AutoCloseable { task.cancel() })
            future
        }
    }
}
