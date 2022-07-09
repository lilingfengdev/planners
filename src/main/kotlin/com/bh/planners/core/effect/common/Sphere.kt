package com.bh.planners.core.effect.common

import org.bukkit.Bukkit
import org.bukkit.Location
import taboolib.common.platform.function.info
import kotlin.math.sqrt

/**
 * 构造一个球
 *
 * 算法来源: https://stackoverflow.com/questions/9600801/evenly-distributing-n-points-on-a-sphere/26127012#26127012
 *
 * @author Zoyn
 */
class Sphere(override var origin: Location, var sample: Int, var radius: Double, spawner: ParticleSpawner) :
    ParticleObj(
        spawner
    ) {
    /**
     * 黄金角度 约等于137.5度
     */
    private val phi = Math.PI * (3.0 - sqrt(5.0))
    private val locations = mutableListOf<Location>()

    constructor(origin: Location, spawner: ParticleSpawner) : this(origin, 50, 1.0, spawner)

    init {
        resetLocations()
    }

    override fun show() {
        locations.forEach { spawnParticle(it) }
    }

    fun setSample(sample: Int): Sphere {
        this.sample = sample
        resetLocations()
        return this
    }

    fun setRadius(radius: Double): Sphere {
        this.radius = radius
        resetLocations()
        return this
    }

    fun resetLocations() {
        locations.clear()
        for (i in 0 until sample) {
            // y goes from 1 to -1
            var y = (1 - i / (sample - 1f) * 2).toDouble()
            // radius at y
            val yRadius = Math.sqrt(1 - y * y)
            // golden angle increment
            val theta = phi * i
            val x = Math.cos(theta) * radius * yRadius
            val z = Math.sin(theta) * radius * yRadius
            y *= radius
            locations.add(origin.clone().add(x, y, z))
        }
    }
}