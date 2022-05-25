package com.bh.planners.api.particle

import org.bukkit.entity.Player
import taboolib.common.platform.sendTo
import taboolib.common.util.Location
import taboolib.common.util.Vector
import taboolib.module.effect.ParticleSpawner
import taboolib.platform.util.toBukkitLocation

class EffectSpawner(val option: EffectOption) : ParticleSpawner {

    override fun spawn(location: Location) {
        val bukkitLocation = location.toBukkitLocation()
        val entities = bukkitLocation.world!!.getNearbyEntities(bukkitLocation, 100.0, 100.0, 100.0)
        entities.filterIsInstance<Player>().forEach { _ ->
            option.particle.sendTo(
                location = location,
                offset = option.offsetVector,
                count = option.count,
                speed = option.speed,
                data = option.data
            )
        }

    }
}
