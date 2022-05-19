package com.bh.planners.api.particle

import org.bukkit.entity.Player
import taboolib.common.platform.ProxyParticle
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.util.Location
import taboolib.common.util.Vector
import taboolib.module.effect.ParticleSpawner

class ParticleImpl : ParticleSpawner {

    val table = mutableMapOf<Key, Any>()

    inline fun <reified T> get(key: Key): T? {
        return table[key] as? T ?: key.value as T
    }

    val type: ProxyParticle
        get() = get(Key.PARTICLE)!!

    val speed: Double
        get() = get(Key.SPEED)!!

    val owner: Player
        get() = get(Key.PLAYER)!!

    val offset: Vector
        get() = get<Array<Double>>(Key.OFFSET)!!.toVector()

    val count: Int
        get() = get(Key.COUNT)!!

    val data: ProxyParticle.Data?
        get() = get(Key.DATA)

    fun add(key: Key, value: Any): ParticleImpl {
        table[key] = value
        return this
    }

    override fun spawn(location: Location) {
        type.sendTo(
            adaptPlayer(owner),
            location,
            offset = offset,
            count = count,
            data = data,
            speed = speed
        )
    }

    enum class Key(val value: Any?) {
        PLAYER(null),
        PARTICLE("FLAME"),
        SPEED(0.0),
        OFFSET(Array(3) { 0.0 }),
        COUNT(1),
        DATA(null)
    }

}