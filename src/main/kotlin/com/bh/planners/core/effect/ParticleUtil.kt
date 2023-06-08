package com.bh.planners.core.effect

import taboolib.common.util.Vector

fun Array<Double>.toVector(): Vector {
    return Vector(this[0], this[1], this[2])
}