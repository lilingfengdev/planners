package com.bh.planners.core.kether.game

import com.bh.planners.api.common.Demand
import com.bh.planners.api.event.EntityEvents
import com.bh.planners.core.effect.Target.Companion.getLivingEntity
import com.bh.planners.core.feature.damageable.DamageableDispatcher
import com.bh.planners.core.kether.*
import com.bh.planners.core.kether.common.CombinationKetherParser
import com.bh.planners.core.kether.common.KetherHelper
import com.bh.planners.core.kether.common.KetherHelper.actionContainerOrSender
import com.bh.planners.core.kether.common.KetherHelper.containerOrEmpty
import com.bh.planners.core.kether.game.damage.AttackProvider
import com.bh.planners.core.kether.game.damage.DamageType
import com.bh.planners.util.eval
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.metadata.FixedMetadataValue
import taboolib.common.platform.function.submit
import taboolib.library.kether.ParsedAction
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.library.reflex.Reflex.Companion.setProperty
import taboolib.module.kether.*
import taboolib.module.nms.MinecraftVersion
import taboolib.platform.BukkitPlugin
import taboolib.platform.util.removeMeta
import taboolib.platform.util.setMeta
import java.util.concurrent.CompletableFuture

@CombinationKetherParser.Used
private fun damage() = KetherHelper.simpleKetherParser<Unit> {
    it.group(
            text(),
            containerOrEmpty(),
            command("source", then = actionContainerOrSender()).option(),
            command("type", then = text()).option().defaultsTo("PHYSICS")
    ).apply(it) { value, container, source, type ->
        val t = DamageType.valueOf(type.uppercase().replace("-", "_"))
        now {
            val source = source?.firstLivingEntityTarget() ?: bukkitPlayer()
            container.forEachLivingEntity {
                val damage = value.eval(getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: 0.0)
                val event = EntityEvents.newInstanceDamageByEntity(source, this, damage, t)
                if (event.call()) {
                    doDamage(source, this, event.value)
                }
            }

        }
    }
}

@CombinationKetherParser.Used
private fun attack() = KetherHelper.simpleKetherParser<Unit> {
    it.group(
            text(),
            command("data", then = text()).option(),
            containerOrEmpty()
    ).apply(it) { text, data, container ->
        now {
            val source = getContext().sender.getLivingEntity() ?: bukkitPlayer() ?: return@now
            container.forEachLivingEntity {
                val damage = text.eval(this.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: 0.0)
                this.setMeta("@planners:attack", true)
                AttackProvider.INSTANCE?.process(this, damage, source, Demand(data ?: "EMPTY"))
                this.removeMeta("@planners:attack")
            }
        }
    }
}

fun doDamage(source: LivingEntity?, entity: LivingEntity, damage: Double) {
    entity.noDamageTicks = 0
    entity.setMeta("@planners:damage", true)

    // 如果实体血量 - 预计伤害值 < 0 提前设置击杀者
    if (source != null && entity.health - damage <= 0) {
        entity.setKiller(source)
        //                EntityDeathEvent(entity, emptyList())
    }
    entity.damage(damage)
    entity.removeMeta("@planners:damage")
}

fun LivingEntity.setKiller(source: LivingEntity) {
    when (MinecraftVersion.major) {
        // 1.12.* 1.16.*
        4, 8 -> setProperty("entity/killer", source.getProperty("entity"))
        // 1.15.* 1.17.* bc
        7, 9 -> setProperty("entity/bc", source.getProperty("entity"))
        // 1.18.2 bc 1.18.1 bd
        10 -> if (MinecraftVersion.minecraftVersion == "v1_18_R2") {
            setProperty("entity/bc", source.getProperty("entity"))
        } else {
            setProperty("entity/bd", source.getProperty("entity"))
        }
        // 1.18.* 1.19.* bd
        11 -> setProperty("entity/bd", source.getProperty("entity"))

    }
}