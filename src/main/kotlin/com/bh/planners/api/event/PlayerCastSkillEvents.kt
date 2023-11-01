package com.bh.planners.api.event

import com.bh.planners.api.common.ExecuteResult
import com.bh.planners.core.pojo.Session
import com.bh.planners.core.pojo.Skill
import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

class PlayerCastSkillEvents {

    class Pre(val player: Player, val session: Session) : BukkitProxyEvent() {

        val playerSkill = session.playerSkill

        val skill = session.skill

    }

    class Post(val player: Player, val skill: Skill) : BukkitProxyEvent()

    class Failure(val player: Player, val skill: Skill, val result: ExecuteResult) : BukkitProxyEvent()

    /**
     * 内部条件检索完毕后的处理事件
     */
    class Record(val player: Player, val session: Session) : BukkitProxyEvent()

}