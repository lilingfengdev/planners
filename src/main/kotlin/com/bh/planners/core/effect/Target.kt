package com.bh.planners.core.effect

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
import java.util.function.Consumer


interface Target {

    val isValid: Boolean
        get() = true

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
            return Entity(this).apply {

            }
        }

        fun org.bukkit.Location.toTarget(): Location {
            return Location(this).apply {

            }
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

    open class Container : LinkedHashSet<Target>() {

        override fun forEach(action: Consumer<in Target>) {
            super.forEach {
                if (it.isValid) action.accept(it)
            }
        }

        fun join(vararg target: Target): Container {
            this += target
            return this
        }

        fun join(targets: List<Target>): Container {
            this += targets
            return this
        }

        fun has(target: Target): Boolean {
            return target in this
        }

        fun unmerge(container: Container): Container {
            if (container.isEmpty()) return this
            removeIf { container.contains(it) }
            return this
        }

        fun merge(container: Container): Container {
            if (container.isEmpty()) return this
            this += container
            return this
        }

        fun remove(amount: Int = 1) {
            (0 until amount).forEach { _ ->
                if (this.isNotEmpty()) {
                    this -= this.random()
                }
            }
        }

        inline fun <reified T> forEach(func: T.(Int) -> Unit) {

            filterIsInstance<T>().forEachIndexed { index, t ->
                func(t, index)
            }

        }

        fun forEachEntity(func: org.bukkit.entity.Entity.(Int) -> Unit) {
            forEach<Entity> { index -> func(entity, index) }
        }

        inline fun <reified T : Target> map(check: T.() -> Boolean): List<T> {
            return this.filterIsInstance<T>().filter(check)
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

        fun firstTarget() = firstOrNull()

        fun firstLivingEntityTarget() = filterIsInstance<Entity>().firstOrNull { it.isLiving }?.asLivingEntity

        fun firstEntityTarget() = filterIsInstance<Entity>().firstOrNull()?.entity

        fun getLocationTarget(index: Int) = filterIsInstance<Location>().getOrNull(index)?.value

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

        override fun toString(): String {
            return "Location(loc=$loc)"
        }


    }

    open class Entity(val entity: org.bukkit.entity.Entity) : Location(null) {

        override val value: org.bukkit.Location
            get() = if (isLiving) asLivingEntity!!.eyeLocation else entity.location

        val isLiving: Boolean
            get() = entity is LivingEntity

        val asLivingEntity: LivingEntity?
            get() = entity as? LivingEntity

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

        override fun toString(): String {
            return "Entity(entity=$entity, type=$type)"
        }

    }

}
