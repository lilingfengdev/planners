package com.bh.planners.core.effect

import org.bukkit.util.Vector
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin


/**
 * 将给定向量绕X轴进行旋转
 *
 * @param v     给定的向量
 * @param angle 旋转角度
 * @return [Vector]
 */
fun rotateAroundAxisX(v: Vector, angle: Double): Vector {
    if (angle == 0.0) return v
    val angdeg = Math.toRadians(angle)
    val cos = cos(angdeg)
    val sin = sin(angdeg)
    val y = v.y * cos - v.z * sin
    val z = v.y * sin + v.z * cos
    return v.setY(y).setZ(z)
}

/**
 * 将给定向量绕Y轴进行旋转
 *
 * @param v     给定的向量
 * @param angle 旋转角度
 * @return [Vector]
 */
fun rotateAroundAxisY(v: Vector, angle: Double): Vector {
    if (angle == 0.0) return v
    val angdeg = Math.toRadians(-angle)
    val cos = cos(angdeg)
    val sin = sin(angdeg)
    val x = v.x * cos + v.z * sin
    val z = v.x * -sin + v.z * cos
    return v.setX(x).setZ(z)
}

/**
 * 将给定向量绕Z轴进行旋转
 *
 * @param v     给定的向量
 * @param angle 旋转角度
 * @return [Vector]
 */
fun rotateAroundAxisZ(v: Vector, angle: Double): Vector {
    if (angle == 0.0) return v
    val angdeg = Math.toRadians(angle)
    val cos = cos(angdeg)
    val sin = sin(angdeg)
    val x = v.x * cos - v.y * sin
    val y = v.x * sin + v.y * cos
    return v.setX(x).setY(y)
}

/**
 * This handles non-unit vectors, with yaw and pitch instead of X,Y,Z angles.
 *
 *
 * Thanks to SexyToad!
 *
 *
 * 将一个非单位向量使用yaw和pitch来代替X, Y, Z的角旋转方式
 *
 * @param v            向量
 * @param yawDegrees   yaw的角度
 * @param pitchDegrees pitch的角度
 * @return [Vector]
 */
fun rotateVector(v: Vector, yawDegrees: Float, pitchDegrees: Float): Vector {
    val yaw = Math.toRadians((-1 * (yawDegrees + 90)).toDouble())
    val pitch = Math.toRadians(-pitchDegrees.toDouble())
    val cosYaw = cos(yaw)
    val cosPitch = cos(pitch)
    val sinYaw = sin(yaw)
    val sinPitch = sin(pitch)
    var initialX = v.x
    val initialY = v.y
    val initialZ = v.z

    // Z_Axis rotation (Pitch)
    var x: Double = initialX * cosPitch - initialY * sinPitch
    val y: Double = initialX * sinPitch + initialY * cosPitch

    // Y_Axis rotation (Yaw)
    initialX = x
    val z: Double = initialZ * cosYaw - initialX * sinYaw
    x = initialZ * sinYaw + initialX * cosYaw
    return Vector(x, y, z)
}

/**
 * 判断一个向量是否已单位化
 *
 * @param vector 向量
 * @return 是否单位化
 */
fun isNormalized(vector: Vector): Boolean {
    return abs(vector.lengthSquared() - 1) < Vector.getEpsilon()
}

/**
 * 空间向量绕任一向量旋转
 *
 * @param vector 待旋转向量
 * @param axis   旋转轴向量
 * @param angle  旋转角度
 * @return [Vector]
 */
fun rotateAroundAxis(
    vector: Vector,
    axis: Vector,
    angle: Double,
): Vector {
    return rotateAroundNonUnitAxis(vector, if (isNormalized(axis)) axis else axis.clone().normalize(), angle)
}

/**
 * 空间向量绕任一向量旋转
 *
 * 注: 这里的旋转轴必须为已单位化才可使用!
 *
 *
 * 罗德里格旋转公式: https://zh.wikipedia.org/wiki/%E7%BD%97%E5%BE%B7%E9%87%8C%E6%A0%BC%E6%97%8B%E8%BD%AC%E5%85%AC%E5%BC%8F
 *
 *
 * 正常人能看懂的: https://www.cnblogs.com/wubugui/p/3734627.html
 *
 * @param vector 要旋转的向量
 * @param axis   旋转轴向量
 * @param angle  旋转角度
 * @return [Vector]
 */
fun rotateAroundNonUnitAxis(
    vector: Vector,
    axis: Vector,
    angle: Double,
): Vector {
    val x = vector.x
    val y = vector.y
    val z = vector.z
    val x2 = axis.x
    val y2 = axis.y
    val z2 = axis.z
    val cosTheta = cos(angle)
    val sinTheta = sin(angle)
    val dotProduct = vector.dot(axis)
    val xPrime = x2 * dotProduct * (1.0 - cosTheta) + x * cosTheta + (-z2 * y + y2 * z) * sinTheta
    val yPrime = y2 * dotProduct * (1.0 - cosTheta) + y * cosTheta + (z2 * x - x2 * z) * sinTheta
    val zPrime = z2 * dotProduct * (1.0 - cosTheta) + z * cosTheta + (-y2 * x + x2 * y) * sinTheta
    return vector.setX(xPrime).setY(yPrime).setZ(zPrime)
}


fun rotateAroundX(vector: Vector, angle: Double): Vector {
    if (angle == 0.0) return vector
    val angleCos = cos(Math.toRadians(angle))
    val angleSin = sin(Math.toRadians(angle))
    val y: Double = angleCos * vector.y - angleSin * vector.z
    val z: Double = angleSin * vector.y + angleCos * vector.z
    return vector.setY(y).setZ(z)
}

fun rotateAroundY(vector: Vector, angle: Double): Vector {
    if (angle == 0.0) return vector
    val angleCos = cos(Math.toRadians(angle))
    val angleSin = sin(Math.toRadians(angle))
    val x: Double = angleCos * vector.x + angleSin * vector.z
    val z: Double = -angleSin * vector.x + angleCos * vector.z
    return vector.setX(x).setZ(z)
}

fun rotateAroundZ(vector: Vector, angle: Double): Vector {
    if (angle == 0.0) return vector
    val angleCos = cos(Math.toRadians(angle))
    val angleSin = sin(Math.toRadians(angle))
    val x: Double = angleCos * vector.x - angleSin * vector.y
    val y: Double = angleSin * vector.x + angleCos * vector.y
    return vector.setX(x).setY(y)
}