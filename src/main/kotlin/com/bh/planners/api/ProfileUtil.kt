package com.bh.planners.api

import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.PlannersAPI.plannersProfileIsLoaded
import com.bh.planners.api.event.*
import com.bh.planners.api.script.ScriptLoader
import com.bh.planners.core.kether.namespaces
import com.bh.planners.core.pojo.*
import com.bh.planners.core.pojo.data.Data
import com.bh.planners.core.pojo.key.IKeySlot
import com.bh.planners.core.pojo.player.PlayerJob
import com.bh.planners.core.pojo.player.PlayerProfile
import com.bh.planners.core.storage.Storage
import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.info
import taboolib.common.platform.function.submitAsync
import taboolib.module.kether.KetherShell
import taboolib.module.kether.ScriptContext
import taboolib.module.kether.runKether
import taboolib.platform.util.sendLang
import java.util.concurrent.CompletableFuture


fun PlayerProfile.addPoint(point: Int) {
    setPoint(this.point + point)
}

fun PlayerProfile.setPoint(point: Int) {
    if (job == null) return
    this.point = point
    submitAsync {
        Storage.INSTANCE.updateJob(player, job!!)
    }
}

fun PlayerProfile.next(skill: PlayerJob.Skill) {
    if (1 + skill.level <= skill.instance.option.levelCap) {
        skill.level++
        PlayerSkillUpgradeEvent(player, skill).call()
        submitAsync {
            Storage.INSTANCE.updateSkill(this, skill)
        }
    }
}

fun PlayerProfile.addExperience(value: Int) {
    if (job == null) return
    val event = PlayerGetExperienceEvent(player, value)
    event.call()
    if (!event.isCancelled) {
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

fun PlayerProfile.updateFlag(key: String, value: Any) {
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
    PlayerSelectedJobEvent(this).call()
}

fun PlayerProfile.getRoute(): Router.Route? {
    val router = job!!.instance.option.router
    return router.routes.firstOrNull { it.jobKey == job!!.jobKey }
}

fun PlayerProfile.isTransfer(): Boolean {
    if (this.job == null) return false
    if (getRoute() == null) return false

    return true
}

val Player.hasJob: Boolean
    get() = plannersProfileIsLoaded && plannersProfile.job != null


val PlayerProfile.hasJob: Boolean
    get() = job != null

/**
 * 满足条件：
 * 已选择职业
 * 并且目标职业在当前路由内
 */
fun PlayerProfile.transfer(target: Job): Boolean {
    if (!isTransfer()) return false
    val transferJob = getTransferJob(this.job!!.instance, target)!!
    // 重定位职业
    this.job!!.jobKey = transferJob.jobKey
    // 删除其他技能
    this.job!!.skills.removeIf { it.key !in transferJob.job.skills }

    // 提取到异步保存
    submitAsync {
        // 重新定位剩余所属技能
        this@transfer.job!!.skills.forEach {
            Storage.INSTANCE.updateSkillJob(player, this@transfer.job!!, it)
        }
        Storage.INSTANCE.updateCurrentJob(this@transfer)
        Storage.INSTANCE.updateJob(player, this@transfer.job!!)
        PlayerSelectedJobEvent(this@transfer).call()
    }

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
    return playerSkill.instance.option.upgradeConditions.filter { -1 in it.indexTo || playerSkill.level in it.indexTo }
}

fun PlayerProfile.bind(skill: PlayerJob.Skill, iKeySlot: IKeySlot) {

    // 取消绑定
    if (skill.keySlot == iKeySlot) {
        val oldKeySlot = skill.keySlot
        skill.shortcutKey = null
        PlayerSkillBindEvent(player, skill, oldKeySlot).call()
    } else {
        // 解绑同快捷键技能
        val orNull = this.getSkills().firstOrNull { it.key != skill.key && it.keySlot == iKeySlot }
        if (orNull != null) {
            bind(orNull, iKeySlot)
        }
        val oldKeySlot = skill.keySlot
        skill.shortcutKey = iKeySlot.key
        PlayerSkillBindEvent(player, skill, oldKeySlot).call()
    }
    submitAsync {
        Storage.INSTANCE.updateSkill(this@bind, skill)
    }

}

fun Condition.consumeTo(viewer: Player) {
    ScriptLoader.createScript(ContextAPI.create(viewer), this.consume ?: return) { }
}

fun Condition.consumeTo(context: Context) {
    ScriptLoader.createScript(context, this.consume ?: return) { }
}