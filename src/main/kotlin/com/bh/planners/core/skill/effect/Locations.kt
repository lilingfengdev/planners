package com.bh.planners.core.skill.effect

import org.bukkit.Location
import org.bukkit.util.Vector


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
    val newX = dx * Math.cos(radians) - dz * Math.sin(radians) + point.x
    val newZ = dz * Math.cos(radians) + dx * Math.sin(radians) + point.z
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
