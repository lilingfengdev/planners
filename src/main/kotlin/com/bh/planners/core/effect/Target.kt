package com.bh.planners.core.effect

import com.bh.planners.api.common.Demand
import com.bh.planners.api.entity.ProxyBukkitEntity
import com.bh.planners.api.entity.ProxyEntity
import com.bh.planners.core.kether.toLocal
import com.bh.planners.core.pojo.Context
import com.bh.planners.core.pojo.Session
import com.bh.planners.core.selector.Selector
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import taboolib.common.platform.function.console
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer


interface Target {

    val isValid: Boolean
        get() = true

    fun toLocal(): String

    companion object {

        val consoleTarget = Console()

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

        fun org.bukkit.entity.Entity.target(): Entity {
            return toTarget()
        }

        fun ProxyEntity.target(): Entity {
            return Target.Entity(this)
        }
        fun org.bukkit.entity.Entity.toTarget(): Entity {
            return Entity(ProxyBukkitEntity(this))
        }

        fun org.bukkit.Location.toTarget(): Location {
            return Location(this).apply {

            }
        }

        fun Target.isPlayer(): Boolean {
            val entity = getLivingEntity() ?: return false
            return entity is Player
        }

        fun Target.getDirection(): Vector {
            return (this as? Entity)?.value?.direction ?: Vector(0, 0, 0)
        }

        fun Target.getLocation(): org.bukkit.Location? {
            return (this as? Location)?.value
        }

        fun Target.getEntity(): org.bukkit.entity.Entity? {
            return (this as? Entity)?.bukkitEntity
        }

        fun Target.getLivingEntity(): LivingEntity? {
            return (this as? Entity)?.bukkitLivingEntity
        }

        fun Target.getPlayer(): Player? {
            return getLivingEntity() as? Player
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
            forEach<Entity> { index -> func(bukkitEntity ?: return@forEach, index) }
        }

        inline fun <reified T : Target> map(check: T.() -> Boolean): List<T> {
            return this.filterIsInstance<T>().filter(check)
        }

        fun forEachLivingEntity(func: LivingEntity.(Int) -> Unit) {
            forEach<Entity> { index ->
                if (isLiving) {
                    func(bukkitLivingEntity!!, index)
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

//        fun firstLocation() = filterIsInstance<Location>()

        fun firstTarget() = firstOrNull()

        fun firstLivingEntityTarget() = filterIsInstance<Entity>().firstOrNull { it.isLiving }?.bukkitLivingEntity

        fun firstProxyEntity(bukkit: Boolean = true) : ProxyEntity? {
            return filterIsInstance<Entity>().firstOrNull { if (bukkit) it.isBukkit else true }?.proxy
        }

        fun firstEntityTarget(bukkit: Boolean = true): org.bukkit.entity.Entity? {
            return filterIsInstance<Entity>().firstOrNull { if (bukkit) it.isBukkit else true }?.bukkitEntity
        }


        fun getLocationTarget(index: Int) = filterIsInstance<Location>().getOrNull(index)

        fun firstLocation() = getLocationTarget(0)?.value

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

    open class Entity(val proxy: ProxyEntity) : Location(null) {

        val isBukkit = proxy is ProxyBukkitEntity

        override val value: org.bukkit.Location
            get() = if (isLiving)
                bukkitLivingEntity!!.eyeLocation
            else
                proxy.location

        val isLiving: Boolean
            get() = (proxy as? ProxyBukkitEntity)?.isLivingEntity ?: false

        val bukkitEntity: org.bukkit.entity.Entity?
            get() = (proxy as? ProxyBukkitEntity)?.instance

        val bukkitLivingEntity: LivingEntity?
            get() = bukkitEntity as? LivingEntity

        val player: Player?
            get() = bukkitLivingEntity as? Player

        val type = proxy.type

        override fun toLocal(): String {
            return proxy.uniqueId.toString()
        }

        override fun hashCode(): Int {
            return proxy.hashCode()
        }


        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return this.proxy == other
        }

        override fun toString(): String {
            return "Entity(entity=$proxy, type=$type)"
        }

    }

    class Console : Target {

        private val bukkitConsole = console()

        override fun toLocal(): String {
            return "console"
        }

    }

}
