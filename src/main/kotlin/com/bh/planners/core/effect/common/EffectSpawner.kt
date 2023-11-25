package com.bh.planners.core.effect.common

import com.bh.planners.core.effect.EffectOption
import com.bh.planners.core.effect.Target
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.material.MaterialData
import taboolib.common.platform.ProxyParticle
import taboolib.common.platform.function.platformLocation
import taboolib.common.util.Vector
import taboolib.module.effect.ParticleSpawner
import taboolib.module.nms.MinecraftVersion
import taboolib.platform.util.toBukkitLocation

open class EffectSpawner(val option: EffectOption, val viewer: Target.Container, val onPeriod: (Location) -> Unit = {}): ParticleSpawner {

    override fun spawn(location: taboolib.common.util.Location) {
        spawn(platformLocation(location) as Location)
    }

    fun spawn(location: Location) {

        onPeriod(location)

        getViewer().forEach { player ->
            player.sendParticle(
                particle = option.particle,
                location = location.add(option.posX, option.posY, option.posZ),
                offset = option.offsetVector,
                count = option.count,
                speed = option.speed,
                data = option.data
            )
        }

    }

    private fun getViewer(): List<Player> {
        return viewer.mapOfPlayer { this }
    }


    companion object {

        fun Player.sendParticle(
            particle: ProxyParticle,
            location: Location,
            offset: Vector,
            count: Int,
            speed: Double,
            data: ProxyParticle.Data?,
        ) {

            val bukkitParticle = try {
                Particle.valueOf(particle.name)
            } catch (ignored: IllegalArgumentException) {
                error("Unsupported particle ${particle.name}")
            }

            // 1.12.2
            if (MinecraftVersion.majorLegacy == 11202 && data != null && data is ProxyParticle.DustData) {
                val color = data.color
                val r = (color.red / 255.0f).toDouble()
                val g = (color.green / 255.0f).toDouble()
                val b = (color.blue / 255.0f).toDouble()
                (0 until count).forEach { _ ->
                    spawnParticle(bukkitParticle, location.x, location.y, location.z, 0, r, g, b)
                }
                return
            }

            spawnParticle(
                bukkitParticle, location, count, offset.x, offset.y, offset.z, speed, when (data) {
                    is ProxyParticle.DustTransitionData -> {
                        Particle.DustTransition(
                            Color.fromRGB(data.color.red, data.color.green, data.color.blue),
                            Color.fromRGB(data.toColor.red, data.toColor.blue, data.toColor.green),
                            data.size
                        )
                    }

                    is ProxyParticle.DustData -> {
                        Particle.DustOptions(
                            Color.fromRGB(data.color.red, data.color.green, data.color.blue),
                            data.size
                        )
                    }

                    is ProxyParticle.ItemData -> {
                        val item = ItemStack(Material.valueOf(data.material))
                        val itemMeta = item.itemMeta!!
                        itemMeta.setDisplayName(data.name)
                        itemMeta.lore = data.lore
                        try {
                            itemMeta.setCustomModelData(data.customModelData)
                        } catch (_: NoSuchMethodError) {
                        }
                        item.itemMeta = itemMeta
                        if (data.data != 0) {
                            item.durability = data.data.toShort()
                        }
                        item
                    }

                    is ProxyParticle.BlockData -> {
                        if (bukkitParticle.dataType == MaterialData::class.java) {
                            MaterialData(Material.valueOf(data.material), data.data.toByte())
                        } else {
                            Material.valueOf(data.material).createBlockData()
                        }
                    }

                    is ProxyParticle.VibrationData -> {
                        Vibration(
                            data.origin.toBukkitLocation(), when (val destination = data.destination) {
                                is ProxyParticle.VibrationData.LocationDestination -> {
                                    Vibration.Destination.BlockDestination(destination.location.toBukkitLocation())
                                }

                                is ProxyParticle.VibrationData.EntityDestination -> {
                                    Vibration.Destination.EntityDestination(Bukkit.getEntity(destination.entity)!!)
                                }

                                else -> error("out of case")
                            }, data.arrivalTime
                        )
                    }

                    else -> null
                }
            )
        }
    }

}
