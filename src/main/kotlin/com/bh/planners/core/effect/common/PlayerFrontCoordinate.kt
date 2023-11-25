package com.bh.planners.core.effect.common

import com.bh.planners.core.effect.util.rotateLocationAboutPoint
import org.bukkit.Location

/**
 * 表示一个将X轴显示在玩家面前的坐标器
 *
 * 自动修正在XZ平面上的粒子朝向
 *
 * @author Zoyn
 */
class PlayerFrontCoordinate(val playerLocation: Location) {

    var originDot: Location = playerLocation

    fun newLocation(x: Double, y: Double, z: Double): Location {
        val location = playerLocation.clone()
        location.pitch = 0f
        return rotateLocationAboutPoint(location.add(-x, y, z), location.yaw.toDouble() - 90, originDot)
    }

}