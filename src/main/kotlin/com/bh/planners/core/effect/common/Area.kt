package com.bh.planners.core.effect.common

import org.bukkit.Location
import org.bukkit.entity.Entity

class Area {


    // 构造一个圆
    class Range(origin: Location, val x: Double, val y: Double, val z: Double) {

        val minX = origin.x - x
        val maxX = origin.x + x

        val minY = origin.y - y
        val maxY = origin.y + y

        val minZ = origin.z - z
        val maxZ = origin.z + z

        fun contains(location: Location): Boolean {
            return minX > location.x && maxX < location.x && minY > location.y && maxY < location.y && minZ > location.z && maxZ < location.z
        }

        fun contains(entity: Entity): Boolean {
            return contains(entity.location)
        }

    }

}