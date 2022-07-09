package com.bh.planners.core.effect.common

import com.bh.planners.core.effect.rotateLocationAboutPoint
import org.bukkit.Location

/**
 * 表示一个将X轴显示在玩家面前的坐标器
 *
 * 自动修正在XZ平面上的粒子朝向
 *
 * @author Zoyn
 */
class PlayerFrontCoordinate(playerLocation: Location) {

    val originDot: Location = playerLocation.clone()

    private val rotateAngle: Double = playerLocation.yaw.toDouble() - 90.0

    init {
        originDot.pitch = 0f
    }

    fun newLocation(x: Double, y: Double, z: Double): Location {
        return rotateLocationAboutPoint(originDot.clone().add(-x, y, z), rotateAngle, originDot)
    }
}