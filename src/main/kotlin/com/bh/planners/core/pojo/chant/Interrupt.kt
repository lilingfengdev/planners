package com.bh.planners.core.pojo.chant

import com.bh.planners.api.common.Id
import com.bh.planners.core.pojo.chant.Process
import com.bh.planners.core.pojo.chant.Process.Default
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import taboolib.common.LifeCycle
import taboolib.common.inject.ClassVisitor
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import java.util.function.Supplier

interface Interrupt {

    @Id("damage")
    object Damage : Interrupt

    @Id("damaged")
    object Damaged : Interrupt

    @Id("interact")
    object Interact : Interrupt

    @Id("move")
    object Move : Interrupt

    @Id("realmove")
    object RealMove : Interrupt

    companion object {

        private val interrupts = mutableMapOf<String, Interrupt>()

        fun getInterrupt(string: String): Interrupt {
            return interrupts[string.replaceFirst("--un", "")] ?: error("No interrupt found for $string")
        }

        fun getInterruptKeys(): Set<String> {
            return interrupts.keys
        }

        @SubscribeEvent
        fun e0(e: PlayerMoveEvent) {
            if (e.to!!.x != e.from.x || e.to!!.y != e.from.y || e.to!!.z != e.from.z) {
                val process = ChantBuilder.getRunningProcessOrNull(e.player)
                if (process != null) {
                    attemptInterrupt(process, RealMove)
                }
            }
        }
        @SubscribeEvent
        fun e1(e: PlayerMoveEvent) {
            val process = ChantBuilder.getRunningProcessOrNull(e.player)
            if (process != null) {
                attemptInterrupt(process, Move)
            }
        }

        @SubscribeEvent
        fun e2(e: PlayerInteractEvent) {
            val process = ChantBuilder.getRunningProcessOrNull(e.player)
            if (process != null) {
                attemptInterrupt(process, Interact)
            }
        }

        @SubscribeEvent
        fun e3(e: EntityDamageByEntityEvent) {

            if (e.damager is Player) {
                val process = ChantBuilder.getRunningProcessOrNull(e.damager as Player)
                if (process != null) {
                    attemptInterrupt(process, Damage)
                }
            }

            if (e.entity is Player) {
                val process = ChantBuilder.getRunningProcessOrNull(e.entity as Player)
                if (process != null) {
                    attemptInterrupt(process, Damaged)
                }
            }
        }


        fun attemptInterrupt(process: Process, interrupt: Interrupt) {
            process as Default
            if (!process.actionBreak && process.tags.contains(interrupt)) {
                process.actionBreak = true
                println("进度${process}被中断了")
            }
        }

        @Awake
        object Visitor : ClassVisitor(0) {

            override fun getLifeCycle(): LifeCycle {
                return LifeCycle.LOAD
            }

            override fun visitEnd(clazz: Class<*>, instance: Supplier<*>?) {
                if (Interrupt::class.java.isAssignableFrom(clazz) && clazz.isAnnotationPresent(Id::class.java)) {
                    interrupts[clazz.getAnnotation(Id::class.java).id] = instance?.get() as Interrupt
                }
            }

        }

    }

}