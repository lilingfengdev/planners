package com.bh.planners.api.event

import com.bh.planners.core.pojo.Skill
import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

class PlayerSkillCooldownEvent {

    /**
     * 顽疾技能冷却缩减
     */
    class Reduce(val player : Player, val skill: Skill, val amount : Long) : BukkitProxyEvent()

    /**
     * 顽疾技能冷却覆盖
     */
    class Set(val player : Player, val skill: Skill, val amount : Long) : BukkitProxyEvent()

    /**
     * 顽疾技能冷却增多
     */
    class Increase(val player : Player, val skill: Skill, val amount : Long) : BukkitProxyEvent()


}