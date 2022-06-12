package com.bh.planners.api.particle

import org.bukkit.Location
import org.bukkit.entity.Player
import taboolib.common.platform.sendTo
import taboolib.module.effect.ParticleSpawner
import taboolib.platform.util.toBukkitLocation
import taboolib.platform.util.toProxyLocation

class EffectSpawner(val option: EffectOption) {

    fun spawn(location: Location) {
        val entities = location.world!!.getNearbyEntities(location, 100.0, 100.0, 100.0)
        entities.filterIsInstance<Player>().forEach { _ ->
            option.particle.sendTo(
                location = location.add(option.posX, option.posY, option.posZ).toProxyLocation(),
                offset = option.offsetVector,
                count = option.count,
                speed = option.speed,
                data = option.data
            )
        }

    }
}
