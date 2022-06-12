package com.bh.planners.api

import com.bh.planners.api.event.PlayerGetExperienceEvent
import com.bh.planners.api.event.PlayerLevelChangeEvent
import com.bh.planners.api.event.PlayerSelectedJobEvent
import com.bh.planners.core.pojo.Job
import com.bh.planners.core.pojo.Router
import com.bh.planners.core.pojo.Skill
import com.bh.planners.core.pojo.data.Data
import com.bh.planners.core.pojo.player.PlayerJob
import com.bh.planners.core.pojo.player.PlayerProfile
import com.bh.planners.core.storage.Storage
import taboolib.platform.util.sendLang


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

fun PlayerProfile.addExperience(value: Int) {
    if (job == null) return
    val event = PlayerGetExperienceEvent(player, value)
    event.call()
    if (event.isCancelled) {
        val mark = job!!.level
        job!!.addExperience(value)
        if (mark != job!!.level) {
            PlayerLevelChangeEvent(player, mark, job!!.level).call()
        }
    }
}

fun PlayerProfile.get(key: String) = getFlag(key)

fun PlayerProfile.getFlag(key: String): Data? {
    return flags[key]
}

fun PlayerProfile.update(key: String, value: Any) {
    flags.update(key, value)
}

fun PlayerProfile.setFlag(key: String, data: Data) {
    flags[key] = data
}

fun PlayerProfile.attemptAcceptJob(job: Job): Boolean {
    if (this.job != null) return false
    this.job = Storage.INSTANCE.createPlayerJob(player, job).get()
    Storage.INSTANCE.updateCurrentJob(this)
    PlayerSelectedJobEvent(this).call()
    return true
}

fun PlayerProfile.addLevel(value: Int) {
    if (job == null) return
    val mark = job!!.level
    job!!.counter.addLevel(value)
    if (mark != job!!.level) {
        PlayerLevelChangeEvent(player, mark, job!!.level).call()
    }
}

fun PlayerProfile.reset() {
    job = null
    Storage.INSTANCE.updateCurrentJob(this)
}

/**
 * 满足条件：
 * 已选择职业
 * 并且目标职业在当前路由内
 */
fun PlayerProfile.transfer(target: Job): Boolean {
    if (this.job == null) return false
    val transferJob = getTransferJob(this.job!!.instance, target) ?: return false
    this.job!!.jobKey = transferJob.jobKey
    this.job!!.skills.removeIf { it.key !in transferJob.job.skills }
    Storage.INSTANCE.updateCurrentJob(this)
    Storage.INSTANCE.updateJob(player, this.job!!)
    return true
}

fun getTransferJob(origin: Job, target: Job): Router.TransferJob? {
    val router = PlannersAPI.routers.firstOrNull { origin.key in it.routes.map { it.jobKey } } ?: return null
    val route = router.routes.first { it.jobKey == origin.key }
    return route.transferJobs.firstOrNull { it.jobKey == target.key }
}

fun hasTransfer(origin: Job, target: Job): Boolean {
    return getTransferJob(origin, target) != null
}

fun getUpgradeConditions(playerSkill: PlayerJob.Skill): List<Skill.UpgradeCondition> {
    return playerSkill.instance.option.upgradeConditions.filter { it.indexTo == playerSkill.level }
}