package com.bh.planners.core.skill.effect.common

import org.bukkit.Location
import org.bukkit.util.Vector
import taboolib.common.platform.function.submit
import taboolib.common.platform.service.PlatformExecutor

/**
 * 表示一条线
 *
 * @author Zoyn
 */
class Line(
    var start: Location, var end: Location, var step: Double, override var period: Long, spawner: ParticleSpawner
) : ParticleObj(spawner), Playable {

    private var vector: Vector? = null
    /**
     * 获取每个粒子之间的间隔
     *
     * @return 也就是循环的步长
     */
    /**
     * 向量长度
     */
    private var length = 0.0
    private var currentStep = 0.0

    /**
     * 构造一条线
     */
    constructor(start: Location, end: Location, spawner: ParticleSpawner) : this(start, end, 0.1, spawner)

    /**
     * 构造一条线
     */
    constructor(start: Location, end: Location, step: Double, spawner: ParticleSpawner) : this(
        start, end, step, 20L, spawner
    )

    /**
     * 构造一条线
     *
     * @param start  线的起点
     * @param end    线的终点
     * @param step   每个粒子之间的间隔 (也即步长)
     * @param period 特效周期(如果需要可以使用)
     */
    init {
        resetVector()
    }

    override fun show() {
        var i = 0.0
        while (i < length) {
            val vectorTemp = vector!!.clone().multiply(i)
            spawnParticle(start.clone().add(vectorTemp))
            i += step
        }
    }

    override fun play() {

        submit(now = false, async = true, delay = 0, period = period, commit = null) {
            if (currentStep > length) {
                cancel()
                return@submit
            }
            currentStep += step
            val vectorTemp = vector!!.clone().multiply(currentStep)
            spawnParticle(start.clone().add(vectorTemp))
        }

    }

    fun callPlay(call: Location.(PlatformExecutor.PlatformTask) -> Unit) {
        submit(now = false, async = false, delay = 0, period = period, commit = null) {
            if (currentStep > length) {
                cancel()
                return@submit
            }
            currentStep += step
            val vectorTemp = vector!!.clone().multiply(currentStep)
            spawnParticle(start.clone().add(vectorTemp).apply {
                call(this, this@submit)
            })
        }
    }

    override fun playNextPoint() {
        currentStep += step
        val vectorTemp = vector!!.clone().multiply(currentStep)
        spawnParticle(start.clone().add(vectorTemp))
        if (currentStep > length) {
            currentStep = 0.0
        }
    }

    /**
     * 利用给定的坐标设置线的起始坐标
     *
     * @param start 起始坐标
     * @return [Line]
     */
    fun setStart(start: Location): Line {
        this.start = start
        resetVector()
        return this
    }

    /**
     * 利用给定的坐标设置线的终点坐标
     *
     * @param end 终点
     * @return [Line]
     */
    fun setEnd(end: Location): Line {
        this.end = end
        resetVector()
        return this
    }

    /**
     * 设置每个粒子之间的间隔
     *
     * @param step 间隔
     * @return [Line]
     */
    fun setStep(step: Double): Line {
        this.step = step
        resetVector()
        return this
    }

    /**
     * 手动重设线的向量
     */
    fun resetVector() {
        vector = end.clone().subtract(start).toVector()
        length = vector!!.length()
        vector!!.normalize()
    }

    companion object {
        fun buildLine(locA: Location, locB: Location, step: Double, spawner: ParticleSpawner) {
            val vectorAB = locB.clone().subtract(locA).toVector()
            val vectorLength = vectorAB.length()
            vectorAB.normalize()
            var i = 0.0
            while (i < vectorLength) {
                spawner.spawn(locA.clone().add(vectorAB.clone().multiply(i)))
                i += step
            }
        }
    }
}