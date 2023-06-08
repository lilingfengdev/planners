package com.bh.planners.core.effect

import org.bukkit.util.Vector


/**
 * 将给定向量绕X轴进行旋转
 *
 * @param v     给定的向量
 * @param angle 旋转角度
 * @return [Vector]
 */
fun rotateAroundAxisX(v: Vector, angle: Double): Vector? {
    if (angle == 0.0) return v
    var angle = angle
    angle = Math.toRadians(angle)
    val cos = Math.cos(angle)
    val sin = Math.sin(angle)
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
    var angle = angle
    angle = -angle
    angle = Math.toRadians(angle)
    val cos = Math.cos(angle)
    val sin = Math.sin(angle)
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
    var angle = angle
    angle = Math.toRadians(angle)
    val cos = Math.cos(angle)
    val sin = Math.sin(angle)
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
    val cosYaw = Math.cos(yaw)
    val cosPitch = Math.cos(pitch)
    val sinYaw = Math.sin(yaw)
    val sinPitch = Math.sin(pitch)
    var initialX: Double
    val initialY: Double
    val initialZ: Double
    var x: Double
    val y: Double
    val z: Double

    // Z_Axis rotation (Pitch)
    initialX = v.x
    initialY = v.y
    x = initialX * cosPitch - initialY * sinPitch
    y = initialX * sinPitch + initialY * cosPitch

    // Y_Axis rotation (Yaw)
    initialZ = v.z
    initialX = x
    z = initialZ * cosYaw - initialX * sinYaw
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
    return Math.abs(vector.lengthSquared() - 1) < Vector.getEpsilon()
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
    val cosTheta = Math.cos(angle)
    val sinTheta = Math.sin(angle)
    val dotProduct = vector.dot(axis)
    val xPrime = x2 * dotProduct * (1.0 - cosTheta) + x * cosTheta + (-z2 * y + y2 * z) * sinTheta
    val yPrime = y2 * dotProduct * (1.0 - cosTheta) + y * cosTheta + (z2 * x - x2 * z) * sinTheta
    val zPrime = z2 * dotProduct * (1.0 - cosTheta) + z * cosTheta + (-y2 * x + x2 * y) * sinTheta
    return vector.setX(xPrime).setY(yPrime).setZ(zPrime)
}


fun rotateAroundX(vector: Vector, angle: Double): Vector {
    if (angle == 0.0) return vector
    val angleCos = Math.cos(angle)
    val angleSin = Math.sin(angle)
    val y: Double = angleCos * vector.getY() - angleSin * vector.getZ()
    val z: Double = angleSin * vector.getY() + angleCos * vector.getZ()
    return vector.setY(y).setZ(z)
}

fun rotateAroundY(vector: Vector, angle: Double): Vector {
    if (angle == 0.0) return vector
    val angleCos = Math.cos(angle)
    val angleSin = Math.sin(angle)
    val x: Double = angleCos * vector.getX() + angleSin * vector.getZ()
    val z: Double = -angleSin * vector.getX() + angleCos * vector.getZ()
    return vector.setX(x).setZ(z)
}

fun rotateAroundZ(vector: Vector, angle: Double): Vector {
    if (angle == 0.0) return vector
    val angleCos = Math.cos(angle)
    val angleSin = Math.sin(angle)
    val x: Double = angleCos * vector.getX() - angleSin * vector.getY()
    val y: Double = angleSin * vector.getX() + angleCos * vector.getY()
    return vector.setX(x).setY(y)
}