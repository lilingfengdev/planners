package com.bh.planners.core.kether.effect.renderer

import com.bh.planners.api.particle.EffectOption
import com.bh.planners.api.particle.EffectSpawner
import com.bh.planners.core.kether.effect.Target
import com.bh.planners.core.kether.effect.applyBukkitVector
import org.bukkit.Location
import taboolib.module.effect.Matrix

abstract class AbstractEffectRenderer(val target: Target, val container: Target.Container, val option: EffectOption) :
    EffectRenderer {


    private var matrix: Matrix? = null

    fun hasMatrix(): Boolean {
        return matrix != null
    }

    fun EffectOption.spawn(origin: Location, location: Location) {
        spawnParticle(this, origin, location)
    }

    /**
     * 通过给定一个坐标就可以使用已经指定的参数来播放粒子
     * @param location 坐标
     */
    fun spawnParticle(option: EffectOption, origin: Location, location: Location) {
        var showLocation = location
        if (hasMatrix()) {
            val vector = location.clone().subtract(origin).toVector()
            val changed = matrix!!.applyBukkitVector(vector)
            showLocation = origin.clone().add(changed)
        }
        EffectSpawner(option).spawn(showLocation)
    }

}