package com.bh.planners.core.effect.common

import org.bukkit.Location
import taboolib.common.platform.function.submit

/**
 * 表示一个弧
 *
 * @author Zoyn
 */
class Arc : ParticleObj, Playable {

    var startAngle = 0.0
        private set
    var angle: Double
        private set
    var radius: Double
        private set
    var step: Double
        private set
    private var currentAngle = 0.0

    constructor(origin: Location, spawner: ParticleSpawner) : this(origin, 30.0, spawner) {}
    constructor(origin: Location, angle: Double, spawner: ParticleSpawner) : this(origin, angle, 1.0, spawner) {}
    constructor(origin: Location, angle: Double, radius: Double, spawner: ParticleSpawner) : this(
        origin, angle, radius, 1.0, spawner
    ) {
    }

    /**
     * 构造一个弧
     *
     * @param origin 弧所在的圆的圆点
     * @param angle  弧所占的角度
     * @param radius 弧所在的圆的半径
     * @param step   每个粒子的间隔(也即步长)
     */
    constructor(
        origin: Location, angle: Double, radius: Double, step: Double, spawner: ParticleSpawner
    ) : this(origin, angle, radius, step, 20L, spawner) {
    }

    /**
     * 从零度角开始构造一个弧
     *
     * @param origin 弧所在的圆的圆点
     * @param angle  弧所占的角度
     * @param radius 弧所在的圆的半径
     * @param step   每个粒子的间隔(也即步长)
     * @param period 特效周期(如果需要可以使用)
     */
    constructor(
        origin: Location, angle: Double, radius: Double, step: Double, period: Long, spawner: ParticleSpawner
    ) : super(
        spawner!!
    ) {

        this.origin = origin
        this.angle = angle
        this.radius = radius
        this.step = step
        this.period = period
    }

    /**
     * 从给定的开始角构造一个弧
     *
     * @param origin     弧所在的圆的圆点
     * @param startAngle 开始角
     * @param angle      弧总共的角
     * @param radius     弧所占半径
     * @param step       每个粒子的间隔
     * @param period     特效周期(如果需要可以使用)
     * @param spawner    粒子生成器
     */
    constructor(
        origin: Location,
        startAngle: Double,
        angle: Double,
        radius: Double,
        step: Double,
        period: Long,
        spawner: ParticleSpawner
    ) : super(
        spawner!!
    ) {
        this.origin = origin
        this.startAngle = startAngle
        this.angle = angle
        this.radius = radius
        this.step = step
        this.period = period
    }

    override fun show() {
        var i = startAngle
        while (i < angle) {
            val radians = Math.toRadians(i)
            val x = radius * Math.cos(radians)
            val z = radius * Math.sin(radians)
            spawnParticle(origin.clone().add(x, 0.0, z))
            i += step
        }
    }

    override fun play() {
        currentAngle = startAngle
        submit(false,false,0,period,null) {
            // 进行关闭
            if (currentAngle > angle) {
                cancel()
                return@submit Unit
            }
            currentAngle += step
            val radians = Math.toRadians(currentAngle)
            val x = radius * Math.cos(radians)
            val z = radius * Math.sin(radians)
            spawnParticle(origin.clone().add(x, 0.0, z))
        }
    }

    override fun playNextPoint() {
        currentAngle += step
        val radians = Math.toRadians(currentAngle)
        val x = radius * Math.cos(radians)
        val z = radius * Math.sin(radians)
        spawnParticle(origin.clone().add(x, 0.0, z))

        // 进行重置
        if (currentAngle > angle) {
            currentAngle = startAngle
        }
    }

    fun setStartAngle(startAngle: Double): Arc {
        this.startAngle = startAngle
        return this
    }

    fun setAngle(angle: Double): Arc {
        this.angle = angle
        return this
    }

    fun setRadius(radius: Double): Arc {
        this.radius = radius
        return this
    }

    fun setStep(step: Double): Arc {
        this.step = step
        return this
    }
}