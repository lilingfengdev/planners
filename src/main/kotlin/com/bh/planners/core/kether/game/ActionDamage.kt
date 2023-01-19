package com.bh.planners.core.kether.game

import com.bh.planners.api.event.EntityEvents
import com.bh.planners.core.kether.*
import com.bh.planners.core.kether.game.damage.AttackProvider
import com.bh.planners.util.eval
import net.minecraft.server.v1_12_R1.Entity
import net.minecraft.server.v1_12_R1.EntityHuman
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.entity.LivingEntity
import org.bukkit.metadata.FixedMetadataValue
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.OptionalEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.info
import taboolib.common.platform.function.submit
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.library.reflex.Reflex.Companion.setProperty
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import taboolib.module.nms.MinecraftVersion
import taboolib.platform.BukkitPlugin
import taboolib.platform.util.hasMeta
import taboolib.platform.util.removeMeta
import taboolib.platform.util.setMeta
import java.util.concurrent.CompletableFuture

class ActionDamage {

    class Damage(val value: ParsedAction<*>, val selector: ParsedAction<*>, val source: ParsedAction<*>?) :
        ScriptAction<Void>() {

        fun execute(entity: LivingEntity, source: LivingEntity?, damage: String) {
            val result = damage.eval(entity.maxHealth)
            val damageByEntityEvent = EntityEvents.DamageByEntity(source, entity, result)
            if (damageByEntityEvent.call()) {
                doDamage(source, entity, damageByEntityEvent.value)
            }

        }

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {

            frame.runTransfer0<String>(value) { damage ->
                frame.createContainer(selector).thenAccept { container ->
                    if (source == null) {
                        val sourceEntity = frame.bukkitPlayer()
                        submit {
                            container.forEachLivingEntity { execute(this, sourceEntity, damage) }
                        }
                    } else {
                        frame.createContainer(source).thenAccept { source ->
                            val sourceEntity = source.firstLivingEntityTarget() ?: return@thenAccept
                            container.forEachLivingEntity { execute(this, sourceEntity, damage) }
                        }
                    }
                }
            }

            return CompletableFuture.completedFuture(null)
        }
    }

    class Attack(val value: ParsedAction<*>, val selector: ParsedAction<*>) : ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            val player = frame.bukkitPlayer() ?: return CompletableFuture.completedFuture(null)
            frame.runTransfer0<String>(value) { damage ->
                frame.createContainer(selector).thenAccept { container ->
                    submit {
                        container.forEachLivingEntity {
                            this.noDamageTicks = 0
                            this.setMetadata("Planners:Attack", FixedMetadataValue(BukkitPlugin.getInstance(), true))
                            AttackProvider.INSTANCE?.doDamage(this, damage.eval(this.maxHealth), player)
                            this.setMetadata("Planners:Attack", FixedMetadataValue(BukkitPlugin.getInstance(), false))
                        }
                    }
                }
            }
            return CompletableFuture.completedFuture(null)
        }
    }

    companion object {

        /**
         * 对selector目标造成伤害
         * damage [damage] [selector]
         * damage 10.0 they ":@aline 10" source ":@self"
         */
        @KetherParser(["damage"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            Damage(it.nextParsedAction(), it.selector(), it.tryGet(arrayOf("source")))
        }

        /**
         * 对selector目标攻击,
         * attack [damage] [selector]
         * attack 10.0 they "-@aline 10"
         */
        @KetherParser(["attack"], namespace = NAMESPACE, shared = true)
        fun parser2() = scriptParser {
            Attack(it.nextParsedAction(), it.selector())
        }

//        @SubscribeEvent(
//            bind = "ac.github.oa.api.event.entity.EntityDamageEvent",
//            ignoreCancelled = true,
//            priority = EventPriority.LOWEST
//        )
//        fun e(ope: OptionalEvent) {
//            val e = ope.get<EntityDamageEvent>()
//            // 如果是来自pl的攻击 则取消
//            if (e.damageMemory.injured.hasMeta("Planners:Damage")) {
//                e.isCancelled = true
//            }
//        }
        fun doDamage(source: LivingEntity?, entity: LivingEntity, damage: Double) {
            entity.noDamageTicks = 0
            entity.setMeta("Planners:Damage", true)

            // 如果实体血量 - 预计伤害值 < 0 提前设置击杀者
            if (source != null && entity.health - damage <= 0) {
                entity.setKiller(source)
            }
            entity.damage(damage)
            entity.removeMeta("Planners:Damage")
        }

        fun LivingEntity.setKiller(source: LivingEntity) {
            when (MinecraftVersion.major) {
                // 1.12.* 1.16.*
                4, 8 -> setProperty("entity/killer", source.getProperty("entity"))
                // 1.18.* 1.19.*
                7, 9 -> setProperty("entity/bc", source.getProperty("entity"))
                // 1.18.* 1.19.* bd
                10, 11 -> setProperty("entity/bd", source.getProperty("entity"))

            }
        }

    }

}