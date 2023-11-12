package com.bh.planners.core.kether.game.damage

import com.bh.planners.api.common.Demand
import com.skillw.fightsystem.FightSystem
import com.skillw.fightsystem.api.fight.FightData
import org.bukkit.entity.LivingEntity

class FightSystemP : AttackProvider {

    override fun process(entity: LivingEntity, damage: Double, source: LivingEntity, demand: Demand) {
        val group = demand.get("group") ?: "default"
        val fightData = FightData(source, entity)
        demand.dataMap.forEach {
            fightData[it.key] = it.value[0]
        }
        FightSystem.fightGroupManager.runFight(group, fightData, message = false, damage = true)
    }
}