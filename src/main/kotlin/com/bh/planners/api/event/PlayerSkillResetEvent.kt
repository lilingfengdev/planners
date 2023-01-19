package com.bh.planners.api.event

import com.bh.planners.core.pojo.Skill
import com.bh.planners.core.pojo.player.PlayerJob
import com.bh.planners.core.pojo.player.PlayerProfile
import taboolib.platform.type.BukkitProxyEvent

class PlayerSkillResetEvent {

    class Pre(val profile: PlayerProfile,val skill: PlayerJob.Skill) : BukkitProxyEvent()

    class Post(val profile: PlayerProfile,val skill: PlayerJob.Skill) : BukkitProxyEvent()

}