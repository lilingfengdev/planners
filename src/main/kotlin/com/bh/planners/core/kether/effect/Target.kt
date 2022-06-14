package com.bh.planners.core.kether.effect

import com.bh.planners.api.particle.Demand
import com.bh.planners.api.particle.EffectOption
import com.bh.planners.core.kether.selector.Selector
import com.bh.planners.core.kether.toLocal
import com.bh.planners.core.pojo.Session
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player


interface Target {

    fun toLocal(): String

    companion object {

        fun Demand.createContainer(target: Target?, session: Session): Container {
            return Container().also { Selector.check(target, session, this, it) }
        }

        fun EffectOption.createContainer(target: Target?, session: Session): Container {

            return Container().also { Selector.check(target, session, this, it) }
        }

        fun LivingEntity.toTarget(): Entity {
            return Entity(this)
        }

        fun org.bukkit.Location.toTarget(): Location {
            return Location(this)
        }

        fun Target.ifLocation(call: Location.() -> Unit) {
            if (this is Location) {
                call(this)
            }
        }

        fun Target.ifEntity(call: Entity.() -> Unit) {
            if (this is Entity) {
                call(this)
            }
        }
    }

    open class Container {

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
            if (container.targets.isEmpty()) return
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

        fun forEachPlayer(func: Player.(Int) -> Unit) {
            forEachEntity {
                var index = 0
                if (this is Player) {
                    func(this, index)
                    index += 1
                }
            }
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

        override fun toLocal(): String {
            return value.toLocal()
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

        override fun toLocal(): String {
            return livingEntity.uniqueId.toString()
        }


    }

}
