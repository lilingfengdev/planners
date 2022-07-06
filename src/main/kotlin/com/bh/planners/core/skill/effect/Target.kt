package com.bh.planners.core.skill.effect

import com.bh.planners.api.common.Demand
import com.bh.planners.core.kether.selector.Selector
import com.bh.planners.core.kether.toLocal
import com.bh.planners.core.pojo.Context
import com.bh.planners.core.pojo.Session
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture


interface Target {

    fun toLocal(): String

    companion object {

        fun Demand.createContainer(target: Target?, context: Context): CompletableFuture<Container> {
            val future = CompletableFuture<Container>()
            val container = Container()
            Selector.check(target, context, this, container).thenAccept {
                future.complete(container)
            }
            return future
        }

        fun EffectOption.createContainer(target: Target?, context: Context): CompletableFuture<Container> {
            return demand.createContainer(target, context)
        }

        fun org.bukkit.entity.Entity.toTarget(): Entity {
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

        fun forEachEntity(func: org.bukkit.entity.Entity.(Int) -> Unit) {
            forEach<Entity> { index -> func(entity, index) }
        }

        fun forEachLivingEntity(func: LivingEntity.(Int) -> Unit) {
            forEach<Entity> { index ->
                if (entity is LivingEntity) {
                    func(entity, index)
                }
            }
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

        fun firstEntityTarget() = targets.filterIsInstance<Entity>().firstOrNull()?.entity

        fun getLocationTarget(index: Int) = targets.filterIsInstance<Location>().getOrNull(index)?.value

        fun firstLocationTarget() = getLocationTarget(0)

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
            if (other is org.bukkit.Location) {
                return this.loc == other
            }

            return true
        }

    }

    class Entity(val entity: org.bukkit.entity.Entity) : Location(null) {

        override val value: org.bukkit.Location
            get() = if (isLiving) asLivingEntity!!.eyeLocation else entity.location

        val isLiving: Boolean
            get() = entity is LivingEntity

        val asLivingEntity: LivingEntity?
            get() = entity as LivingEntity

        val type = entity.type

        override fun toLocal(): String {
            return entity.uniqueId.toString()
        }

        override fun hashCode(): Int {
            return entity.hashCode()
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other is org.bukkit.entity.Entity) {
                return this.entity == other
            }

            return true
        }

    }

}
