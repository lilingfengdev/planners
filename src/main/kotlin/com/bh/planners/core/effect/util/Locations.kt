package com.bh.planners.core.effect.util

import org.bukkit.Location
import org.bukkit.util.Vector
import kotlin.math.*


/**
 * 在二维平面上利用给定的中心点逆时针旋转一个点
 *
 * @param location 待旋转的点
 * @param angle    旋转角度
 * @param point    中心点
 * @return [Location]
 */
fun rotateLocationAboutPoint(location: Location, angle: Double, point: Location): Location {
    val radians = Math.toRadians(angle)
    val dx = location.x - point.x
    val dz = location.z - point.z
    val newX = dx * cos(radians) - dz * sin(radians) + point.x
    val newZ = dz * cos(radians) + dx * sin(radians) + point.z
    return Location(location.world, newX, location.y, newZ)
}

/**
 * 将一个点围绕另一个向量旋转
 *
 * @param location 给定的点
 * @param origin   向量起始点
 * @param angle    旋转角度
 * @param axis     旋转轴
 * @return [Location]
 */
fun rotateLocationAboutVector(location: Location, origin: Location, angle: Double, axis: Vector): Location {
    val vector = location.clone().subtract(origin).toVector()
    return origin.clone().add(rotateAroundAxis(vector, axis, angle))
}

/**
 * 判断一个是否处在另一个坐标面向的锥形区域内
 *
 *
 * 通过反三角算向量夹角的算法
 *
 * @param target       目标坐标
 * @param livingEntity 实体
 * @param radius       扇形半径
 * @param angle        扇形角度
 * @return 如果处于扇形区域则返回 true
 */
fun isPointInEntitySector(target: Location, location: Location, radius: Double, angle: Double): Boolean {
    val v1 = location.direction
    val v2 = target.clone().subtract(location).toVector()
    val cosTheta = v1.dot(v2) / (v1.length() * v2.length())
    val degree = Math.toDegrees(acos(cosTheta))
    // 距离判断
    return if (target.distance(location) <= radius) {
        // 向量夹角判断
        degree <= angle * 0.5f
    } else false
}

fun Location.isInSphere(origin: Location, radius: Double): Boolean {
    return distance(origin) <= radius
}

fun Location.isInRound(origin: Location, radius: Double): Boolean {
    return clone().also { it.y = 0.0 }.distance(origin.clone().also { it.y = 0.0 }) <= radius
}

// 辅助函数，定义一个点是否在两个值之间
fun within(v: Double, bound1: Double, bound2: Double): Boolean =
    v in minOf(bound1, bound2)..maxOf(bound1, bound2)


// 函数，判断点是否在立方体内
fun isPointInsideCuboid(p: Location, corners: Array<Location>): Boolean {
    val xs = corners.map { it.x }
    val ys = corners.map { it.y }
    val zs = corners.map { it.z }

    return within(p.x, xs.minOrNull()!!, xs.maxOrNull()!!) &&
            within(p.y, ys.minOrNull()!!, ys.maxOrNull()!!) &&
            within(p.z, zs.minOrNull()!!, zs.maxOrNull()!!)
}

fun Location.safeDistance(loc: Location): Double {
    return if (this.world!!.name == loc.world!!.name) {
        distance(loc)
    } else {
        Double.MAX_VALUE
    }
}