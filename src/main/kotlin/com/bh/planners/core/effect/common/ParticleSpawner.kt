package com.bh.planners.core.effect.common

import org.bukkit.Location


/**
 * TabooLib
 * taboolib.module.effect.ParticleGenerator
 *
 * @author sky
 * @since 2021/6/30 11:43 下午
 */
interface ParticleSpawner {

    fun spawn(location: Location)
}