package com.bh.planners.api

import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.core.pojo.player.PlayerJob
import com.bh.planners.core.pojo.player.PlayerProfile
import com.bh.planners.core.storage.Storage


fun PlayerProfile.addPoint(point: Int) {
    setPoint(this.point + point)
}

fun PlayerProfile.setPoint(point: Int) {
    if (job == null) return
    this.point = point
    Storage.INSTANCE.updateJob(player, job!!)
}

fun PlayerProfile.next(skill: PlayerJob.Skill) {
    if (1 + skill.level < skill.instance.option.levelCap) {
        skill.level++
        Storage.INSTANCE.updateSkill(skill)
    }
}