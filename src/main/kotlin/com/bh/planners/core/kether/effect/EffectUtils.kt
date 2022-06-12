package com.bh.planners.core.kether.effect

import com.bh.planners.api.PlannersOption
import org.bukkit.Location
import org.bukkit.entity.LivingEntity
import org.bukkit.util.Vector
import taboolib.module.effect.Matrix
import kotlin.math.cos
import kotlin.math.sin

fun Matrix.applyBukkitVector(vector: Vector): Vector {
    if (row == 2 && column == 2) {
        return applyInBukkit2DVector(vector)
    } else if (row == 3 && column == 3) {
        return applyInBukkit3DVector(vector)
    }

    throw IllegalArgumentException("当前矩阵非 2*2 或 3*3 的方阵")

}

fun Matrix.applyInBukkit2DVector(vector: Vector): Vector {
    val x = vector.x
    val z = vector.z
    val ax = asArray[0][0] * x
    val ay = asArray[0][1] * z

    val bx = asArray[1][0] * x
    val by = asArray[1][1] * z
    return Vector(ax + ay, vector.y, bx + by)
}

fun Matrix.applyInBukkit3DVector(vector: Vector): Vector {
    val x = vector.x
    val y = vector.y
    val z = vector.z

    val ax = asArray[0][0] * x
    val ay = asArray[0][1] * y
    val az = asArray[0][2] * z

    val bx = asArray[1][0] * x
    val by = asArray[1][1] * y
    val bz = asArray[1][2] * z

    val cx = asArray[2][0] * x
    val cy = asArray[2][1] * y
    val cz = asArray[2][2] * z

    return Vector(ax + ay + az, bx + by + bz, cx + cy + cz)
}

fun Location.capture(): List<LivingEntity> {
    return world!!.getNearbyEntities(
        this,
        PlannersOption.scopeThreshold[0],
        PlannersOption.scopeThreshold[1],
        PlannersOption.scopeThreshold[2]
    ).filterIsInstance<LivingEntity>()
}


fun Vector.rotateAroundAxisY(angle: Double): Vector {
    var angle = angle
    angle = -angle
    angle = Math.toRadians(angle)
    val cos = cos(angle)
    val sin = sin(angle)
    val x = this.x * cos + this.z * sin
    val z = this.x * -sin + this.z * cos
    return this.setX(x).setZ(z)
}
