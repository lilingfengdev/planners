package com.bh.planners.core.kether.effect

import com.bh.planners.api.particle.EffectOption
import com.bh.planners.core.kether.effect.selector.Selector
import org.bukkit.Location
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.common.util.random
import kotlin.math.min


interface Target {

    companion object {

        fun EffectOption.createContainer(sender: Player): Container {

            return Container().also { Selector.check(sender, this, it) }
        }

        fun LivingEntity.toTarget(): Entity {
            return Entity(this)
        }

        fun org.bukkit.Location.toTarget(): Location {
            return Location(this)
        }

    }

    class Container {

        val targets = mutableSetOf<Target>()

        val size: Int
            get() = targets.size

        fun add(vararg target: Target) {
            this.targets += target
        }

        fun addAll(targets: List<Target>) {
            this.targets += targets
        }

        fun merge(container: Container) {
            targets += container.targets
        }

        fun clearAll() {
            this.targets.clear()
        }

        fun removeIf(check: Target.() -> Boolean) {
            targets.removeIf { check(it) }
        }

        fun remove(amount: Int = 1) {
            (0 until amount).forEach { _ ->
                if (targets.isNotEmpty()) {
                    targets.remove(targets.random())
                }
            }
        }

        inline fun <reified T> forEach(func: T.(Int) -> Unit) {

            targets.filterIsInstance<T>().forEachIndexed { index, t ->
                func(t, index)
            }
        }

        fun forEachEntity(func: LivingEntity.(Int) -> Unit) {
            forEach<Entity> { index -> func(livingEntity, index) }
        }

        fun forEachLocation(func: org.bukkit.Location.(Int) -> Unit) {
            forEach<Location> { index -> func(value, index) }
        }

        fun firstTarget() = targets.firstOrNull()

        fun firstEntityTarget() = targets.filterIsInstance<Entity>().firstOrNull()?.livingEntity

        fun firstLocationTarget() = targets.filterIsInstance<Location>().firstOrNull()?.value

    }

    open class Location(private val loc: org.bukkit.Location?) : Target {

        open val value: org.bukkit.Location
            get() = loc!!

        override fun hashCode(): Int {
            return value.hashCode()
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Location

            if (loc != other.value) return false

            return true
        }

    }

    class Entity(val livingEntity: LivingEntity) : Location(null) {

        override val value: org.bukkit.Location
            get() = livingEntity.eyeLocation


    }

}
