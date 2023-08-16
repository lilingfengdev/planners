package com.bh.planners.core.kether

import com.bh.planners.core.kether.common.CombinationKetherParser
import com.bh.planners.core.kether.common.KetherHelper
import java.util.UUID

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