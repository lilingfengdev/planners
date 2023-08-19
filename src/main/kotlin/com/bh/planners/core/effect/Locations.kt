package com.bh.planners.core.effect

import org.bukkit.Location
import org.bukkit.util.Vector
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin


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
 * 判断一个是否处在另一个坐标面向的扇形区域内
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
    return if (target.distance(location) < radius) {
        // 向量夹角判断
        degree < angle * 0.5f
    } else false
}

fun Location.isInSphere(origin: Location, radius: Double): Boolean {
    return distance(origin) <= radius
}
fun Location.isInAABB(aa: Location, bb: Location): Boolean {
    val aaX = aa.x
    val aaY = aa.y
    val aaZ = aa.z
    val bbX = bb.x
    val bbY = bb.y
    val bbZ = bb.z
    return if ( x !in (if (aaX < bbX) aaX..bbX else bbX..aaX)) {
        false
    } else if (y !in (if (aaY < bbY) aaY..bbY else bbY..aaY)) {
        false
    } else z in (if (aaZ < bbZ) aaZ..bbZ else bbZ..aaZ)
}

fun isInsideSector(target: Location, origin: Location, radius: Double, angle: Double): Boolean {
    val sectorStart: Vector = rotateAroundAxisY(origin.direction.clone(), -angle / 2)
    val sectorEnd: Vector = rotateAroundAxisY(origin.direction.clone(), angle / 2)
    val v = target.clone().subtract(origin).toVector()
    val start = -sectorStart.x * v.z + sectorStart.z * v.x > 0
    val end = -sectorEnd.x * v.z + sectorEnd.z * v.x > 0
    return !start && end && target.distance(origin) < radius
}

fun Location.safeDistance(loc: Location): Double {
    return if (this.world!!.name == loc.world!!.name) {
        distance(loc)
    } else {
        Double.MAX_VALUE
    }
}