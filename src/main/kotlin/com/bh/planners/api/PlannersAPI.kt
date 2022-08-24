package com.bh.planners.api

import com.bh.planners.api.ManaCounter.takeMana
import com.bh.planners.api.ManaCounter.toCurrentMana
import com.bh.planners.api.common.ExecuteResult
import com.bh.planners.api.event.PlayerCastSkillEvent
import com.bh.planners.api.event.PlayerKeydownEvent
import com.bh.planners.core.kether.namespaces
import com.bh.planners.core.kether.rootVariables
import com.bh.planners.core.pojo.*
import com.bh.planners.core.pojo.player.PlayerProfile
import com.bh.planners.core.pojo.key.IKeySlot
import com.bh.planners.core.pojo.player.PlayerJob
import com.google.gson.Gson
import org.bukkit.entity.Player
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.adaptPlayer
import taboolib.common5.Coerce
import taboolib.module.kether.KetherShell
import taboolib.module.kether.ScriptContext
import taboolib.module.kether.printKetherErrorMessage
import java.util.*

object PlannersAPI {

    val gson = Gson()

    val routers = mutableListOf<Router>()

    val skills = mutableListOf<Skill>()

    val jobs = mutableListOf<Job>()

    val profiles = mutableMapOf<UUID, PlayerProfile>()

    val keySlots = mutableListOf<IKeySlot>()

    val Player.plannersProfile: PlayerProfile
        get() = profiles[uniqueId]!!

    val Player.plannersProfileIsLoaded: Boolean
        get() = profiles.containsKey(uniqueId)


    fun cast(player: Player, skillName: String, mark: Boolean = true): ExecuteResult {
        return player.plannersProfile.cast(skillName, mark)
    }

    fun cast(player: Player, skill: Skill, mark: Boolean = true): ExecuteResult {
        return player.plannersProfile.cast(skill, mark)
    }

    fun cast(player: Player, skill: PlayerJob.Skill, mark: Boolean = true): ExecuteResult {
        return cast(player, skill.instance, mark)
    }

    fun PlayerProfile.cast(skillName: String, mark: Boolean = true): ExecuteResult {
        return cast(skills.firstOrNull { it.key == skillName } ?: error("Skill '${skillName}' not found."), mark)
    }

    fun getRouter(routerKey: String): Router {
        return routers.firstOrNull { it.key == routerKey } ?: error("Router '${routerKey}' not found.")
    }

    fun callKeyByGroup(player: Player, keyGroup: String) {
        this.callKey(player, keySlots.firstOrNull { it.group == keyGroup } ?: return)
    }

    fun callKeyById(player: Player, keyId: String) {
        this.callKey(player, keySlots.firstOrNull { it.key == keyId } ?: return)
    }

    fun callKey(player: Player, slot: IKeySlot) {
        PlayerKeydownEvent(player, slot).call()
    }


    fun PlayerProfile.cast(skill: Skill, mark: Boolean = true): ExecuteResult {

        if (!hasCast(skill)) return ExecuteResult.LEVEL_ZERO

        val session = ContextAPI.createSession(player, skill)
        // 不计入任何标记
        if (!mark) {
            session.cast()
            session.closed = true
            return ExecuteResult.SUCCESS
        }

        val preEvent = PlayerCastSkillEvent.Pre(player, skill).apply { call() }
        if (preEvent.isCancelled) return ExecuteResult.CANCELED

        if (!Counting.hasNext(player, skill)) return ExecuteResult.COOLING

        val mana = Coerce.toDouble(session.mpCost.get())
        if (toCurrentMana() < mana) return ExecuteResult.MANA_NOT_ENOUGH

        Counting.reset(player, session)
        takeMana(mana)
        PlayerCastSkillEvent.Record(player, skill).call()

        session.cast()
        session.closed = true

        PlayerCastSkillEvent.Post(player, skill).call()
        return ExecuteResult.SUCCESS
    }

    fun getSkill(skillName: String): Skill? {
        return skills.firstOrNull { it.key == skillName }
    }

    fun checkUpgrade(player: Player, playerSkill: PlayerJob.Skill): Boolean {
        return getUpgradeConditions(playerSkill).any { return checkCondition(player, playerSkill, it) }
    }

    fun tryUpgrade(player: Player, playerSkill: PlayerJob.Skill): Boolean {

        // 如果满级 则失败
        if (playerSkill.isMax) return false

        // 如果不满足条件 则失败
        if (!checkUpgrade(player, playerSkill)) return false

        getUpgradeConditions(playerSkill).forEach {
            it.consumeTo(player) {
                rootFrame().rootVariables()["@Context"] = Context.Impl(adaptPlayer(player), playerSkill.instance)
                rootFrame().rootVariables()["level"] = playerSkill.level
            }
        }
        player.plannersProfile.next(playerSkill)
        return true
    }


    val PlayerJob.Skill.isMax: Boolean
        get() = level == instance.option.levelCap

    fun dissatisfyUpgrade(player: Player, playerSkill: PlayerJob.Skill): List<Skill.UpgradeCondition> {
        val listOf = mutableListOf<Skill.UpgradeCondition>()

        getUpgradeConditions(playerSkill).forEach {
            if (!checkCondition(player, playerSkill, it)) {
                listOf.add(it)
            }
        }
        return listOf
    }


    fun checkCondition(player: Player, playerSkill: PlayerJob.Skill, it: Condition): Boolean {
        return checkCondition(player, it) {
            rootFrame().rootVariables()["level"] = playerSkill.level
            rootFrame().rootVariables()["@Skill"] = playerSkill
        }
    }

    fun checkCondition(player: Player, condition: Condition, context: ScriptContext.() -> Unit): Boolean {
        return try {
            KetherShell.eval(condition.condition, sender = adaptPlayer(player), namespace = namespaces) {
                context(this)
            }.thenApply { Coerce.toBoolean(it) }.get()
        } catch (e: Throwable) {
            e.printKetherErrorMessage()
            false
        }
    }

    fun hasJob(key: String) = key in jobs.map { it.key }

    fun getJob(key: String) = jobs.firstOrNull { it.key == key } ?: error("Job '${key}' not found.")

    fun getRouterStartJob(router: Router): Job {
        return jobs.firstOrNull { it.key == router.start } ?: error("Job '${router.start}' not found.")
    }


    fun PlayerProfile.hasCast(skill: Skill): Boolean {
        return (getSkill(skill.key)?.level ?: 0) > 0
    }

    @SubscribeEvent
    fun e(e: PlayerKeydownEvent) {
        val player = e.player
        if (player.hasJob) {
            val skill = player.plannersProfile.getSkill(e.keySlot) ?: return
            cast(e.player, skill).handler(player, skill.instance)
        }
    }

}
