package com.bh.planners.core.feature.presskey

import com.bh.planners.api.PlannersAPI
import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.PlannersAPI.plannersProfileIsLoaded
import com.bh.planners.api.PlannersOption
import com.bh.planners.api.common.Baffle
import com.bh.planners.core.pojo.Skill
import org.bukkit.entity.Player
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.info
import taboolib.platform.type.BukkitProxyEvent
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.collections.HashSet
import kotlin.math.max

object Emitter {

    val timeout: Long
        get() = PlannersOption.root.getLong("presskey-patch.timeout", 1000)

    val globalType: String
        get() = PlannersOption.root.getString("presskey-patch.type", "disorderly")!!.uppercase()

    val subscribers = Collections.synchronizedMap(mutableMapOf<Player, MutableList<Subscriber>>())

    val ORDERLY = object : Decider {
        override fun accept(player: Player, list: List<Int>): Boolean {
            // 取所需元素
            val nodes = getUsableUnits(player).filter { it.name in list }
            val baffles = nodes.map { it.name }
            if (baffles.isEmpty()) return false

            // 首位下标
            val first = list[0]
            val indexOf = baffles.indexOfLast { it == first }
            if (indexOf == -1 || baffles.size - indexOf < list.size) return false

            val values = baffles.subList(indexOf, indexOf + list.size)
            if (values.size < baffles.size) return false
            list.forEachIndexed { index, value ->
                if (value != values[index]) {
                    return false
                }
            }
            nodes.forEach { it.consume = true }
            return true
        }

    }

    val DISORDERLY = object : Decider {

        override fun accept(player: Player, list: List<Int>): Boolean {
            // 取所需元素
            val nodes = getUsableUnits(player).filter { it.name in list }
            val baffles = nodes.map { it.name }.toMutableList()
            if (baffles.isEmpty()) return false
            val consumes = mutableListOf<Int>()
            list.forEach {
                val indexOf = baffles.indexOf(it)
                if (indexOf == -1) {
                    return false
                }
                consumes += indexOf
                baffles.removeAt(indexOf)
            }
            consumes.forEach {
                nodes[it].consume = true
            }
            return true
        }

    }

    val Skill.type: String
        get() = config.getString("type", globalType)!!.uppercase()


    @SubscribeEvent
    fun e(e: PressKeyEvents.Get) {
        if (e.packet.action == 1 && e.player.plannersProfileIsLoaded) {
            registerUnit(e.player, Node(e.packet.key, timeout))
            KeyDownEvent(e.player, e.packet.key).call()
        }
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun e(e: KeyDownEvent) {
        tryCall(e.player)
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun e0(e: KeyDownEvent) {
        getUsableSubscribers(e.player, e.key).forEach {
            it.consume = true
            it.complete(null)
        }
    }

    val Skill.decider: Decider?
        get() = when (type) {
            "DISORDERLY" -> DISORDERLY
            "ORDERLY" -> ORDERLY
            else -> null
        }

    fun tryCall(player: Player) {
        val plannersProfile = player.plannersProfile
        plannersProfile.getSkills().forEach {
            if (it.shortcutKey == null) return@forEach
            val tryDecide = tryDecide(player, it.instance.decider ?: return@forEach, it.keySlot!!.groups)
            if (tryDecide) {
                PlannersAPI.cast(player, it).handler(player, it.instance)
            }
        }
    }

    val units = Collections.synchronizedMap(mutableMapOf<Player, MutableList<Node>>())

    fun tryDecide(player: Player, type: Decider, list: List<Int>): Boolean {
        if (list.isEmpty()) return false
        return type.accept(player, list)
    }

    fun getUnits(player: Player) = units.computeIfAbsent(player) { mutableListOf() }

    fun getUsableUnits(player: Player): List<Node> {
        val baffles = getUnits(player)
        baffles.removeIf { !it.isValid }
        return baffles
    }

    fun getSubscribers(player: Player): MutableList<Subscriber> {
        return subscribers.computeIfAbsent(player) { Collections.synchronizedList(mutableListOf()) }
    }

    fun getUsableSubscribers(player: Player): MutableList<Subscriber> {
        val subscribers = getSubscribers(player)
        subscribers.removeIf { !it.isValid }
        return subscribers
    }

    fun getUsableSubscribers(player: Player, key: Int): List<Subscriber> {
        val usableSubscribers = getUsableSubscribers(player)
        return usableSubscribers.filter { it.key == key }
    }

    fun registerSubscribers(player: Player, key: Int, timeout: Long = this.timeout): CompletableFuture<Void> {
        return Subscriber(key, timeout).also { getSubscribers(player) += it }
    }

    fun registerUnit(player: Player, unit: Node) {
        getUnits(player) += unit
    }

    interface Decider {

        fun accept(player: Player, list: List<Int>): Boolean

    }

    class Subscriber(val key: Int, val timeout: Long) : CompletableFuture<Void>() {

        val baffle = Baffle(key, timeout)

        val isValid: Boolean
            get() = !consume && !baffle.next

        var consume = false

        override fun toString(): String {
            return "Subscriber(key=$key, timeout=$timeout, isValid=$isValid, consume=$consume)"
        }


    }

    class Node(val name: Int, val timeout: Long) {

        val create = System.currentTimeMillis()

        val end: Long
            get() = timeout + create

        val countdown: Long
            get() = max(end - System.currentTimeMillis(), 0)

        val isValid: Boolean
            get() = !consume && countdown > 0L

        var consume = false

    }

    class KeyDownEvent(val player: Player, val key: Int) : BukkitProxyEvent()

}