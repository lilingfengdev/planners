package com.bh.planners.core.kether.game.damage

import ac.github.oa.api.OriginAttributeAPI
import ac.github.oa.api.event.entity.ProxyDamageEvent
import ac.github.oa.internal.base.enums.PriorityEnum
import ac.github.oa.internal.core.attribute.impl.Damage
import com.bh.planners.api.common.Demand
import com.bh.planners.core.kether.game.doDamage
import io.lumine.xikage.mythicmobs.MythicMobs
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter
import io.lumine.xikage.mythicmobs.skills.SkillTrigger
import io.lumine.xikage.mythicmobs.skills.TriggeredSkill
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import taboolib.common5.cdouble

class OriginP : AttackProvider {

    val defaultCause = DamageCause.ENTITY_ATTACK

    val isMythicMobs by lazy { Bukkit.getPluginManager().isPluginEnabled("MythicMobs") }

    override fun process(entity: LivingEntity, damage: Double, source: LivingEntity, demand: Demand) {

        val event = ProxyDamageEvent(EntityDamageByEntityEvent(source, entity, defaultCause, 0.0))
        if (demand.dataMap.containsKey("cause")) {
            event.customCause = demand.get("cause")!!.toString()
        }

        val context = event.createDamageContext()
        // 兼容力度
        context.vigor = damage
        context.cause = event.customCause
        demand.dataMap.forEach {
            context.labels[it.key] = it.value.firstOrNull() ?: return@forEach
        }

        if (demand.dataMap.containsKey("damage")) {
            when (event.customCause) {
                "physics" -> {
                    context.addDamage(Damage.physical, demand.get("damage").cdouble)
                }
                "magic" -> {
                    context.addDamage(Damage.magic, demand.get("damage").cdouble)
                }
                else -> {
                    context.addDamage(event.customCause, demand.get("damage").cdouble)
                }
            }
        }


        if (ac.github.oa.api.event.entity.EntityDamageEvent(context, PriorityEnum.PRE).call()) {
            OriginAttributeAPI.callDamage(context)
            if (ac.github.oa.api.event.entity.EntityDamageEvent(context, PriorityEnum.POST).call()) {
                doDamage(source, entity, context.totalDamage.coerceAtLeast(0.0))
                // 兼容onDamage
                if (isMythicMobs) {
                    val mob = MythicMobs.inst().mobManager.getMythicMobInstance(entity) ?: return
                    TriggeredSkill(SkillTrigger.DAMAGED, mob, BukkitAdapter.adapt(entity))
                }
            }
        }

    }


}