package com.bh.planners.api

import com.bh.planners.api.ManaCounter.takeMana
import com.bh.planners.api.ManaCounter.toCurrentMana
import com.bh.planners.api.PlannersLoader.toYamlName
import com.bh.planners.api.common.ExecuteResult
import com.bh.planners.api.compat.PlaceholderKether
import com.bh.planners.api.compat.WorldGuardHook
import com.bh.planners.api.enums.UpgradeResult
import com.bh.planners.api.event.PlayerCastSkillEvents
import com.bh.planners.api.event.PlayerKeydownEvent
import com.bh.planners.api.event.PlayerSkillResetEvent
import com.bh.planners.api.script.ScriptLoader
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.kether.namespaces
import com.bh.planners.core.pojo.*
import com.bh.planners.core.pojo.key.IKeySlot
import com.bh.planners.core.pojo.player.PlayerJob
import com.bh.planners.core.pojo.player.PlayerProfile
import com.bh.planners.core.storage.Storage
import com.bh.planners.util.getScriptFactor
import com.bh.planners.util.runKetherThrow
import com.google.gson.Gson
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.info
import taboolib.common.platform.function.submit
import taboolib.common.platform.function.submitAsync
import taboolib.common5.Coerce
import taboolib.common5.cbool
import taboolib.common5.mirrorNow
import taboolib.module.configuration.Configuration
import taboolib.module.kether.KetherShell.eval
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.ScriptOptions
import taboolib.module.kether.runKether
import taboolib.platform.util.sendLang
import java.io.File
import java.util.*
import java.util.concurrent.CompletableFuture

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
        return player.plannersProfile.castInMirror(skill, mark)
    }

    fun cast(player: Player, skill: PlayerJob.Skill, mark: Boolean = true): ExecuteResult {
        return cast(player, skill.instance, mark)
    }

    fun PlayerProfile.cast(skillName: String, mark: Boolean = true): ExecuteResult {
        return castInMirror(skills.firstOrNull { it.key == skillName } ?: error("Skill '${skillName}' not found."), mark)
    }

    fun getRouter(routerKey: String): Router {
        return routers.firstOrNull { it.key == routerKey } ?: error("Router '${routerKey}' not found.")
    }

    fun callKeyByGroup(player: Player, keyGroup: String) {
        this.callKey(player, keySlots.firstOrNull { it.getGroup(player) == keyGroup } ?: return)
    }

    fun callKeyById(player: Player, keyId: String) {
        this.callKey(player, keySlots.firstOrNull { it.key == keyId } ?: return)
    }

    fun callKey(player: Player, slot: IKeySlot) {
        PlayerKeydownEvent(player, slot).call()
    }

    fun PlayerProfile.castInMirror(skill: Skill, mark: Boolean = true): ExecuteResult {
        return mirrorNow("释放技能") {
            // 该方法体的运行结果会返回给 mirrorNow 方法
            cast(skill, mark)
        }
    }

    fun PlayerProfile.cast(skill: Skill, mark: Boolean = true): ExecuteResult {

        if (!hasCast(skill)) {
            PlayerCastSkillEvents.Failure(player, skill, ExecuteResult.LEVEL_ZERO).call()
            return ExecuteResult.LEVEL_ZERO
        }

        val session = ContextAPI.createSession(player, skill)
        // 不计入任何标记
        if (!mark) {
            session.cast()
            session.closed = true
            return ExecuteResult.SUCCESS
        }

        val preEvent = PlayerCastSkillEvents.Pre(player, skill).apply { call() }
        if (preEvent.isCancelled) {
            PlayerCastSkillEvents.Failure(player, skill, ExecuteResult.CANCELED).call()
            return ExecuteResult.CANCELED
        }

        if (!Counting.hasNext(player, skill)) {
            PlayerCastSkillEvents.Failure(player, skill, ExecuteResult.COOLING).call()
            return ExecuteResult.COOLING
        }

        val mana = Coerce.toDouble(session.mpCost.get())
        if (toCurrentMana() < mana) {
            PlayerCastSkillEvents.Failure(player, skill, ExecuteResult.MANA_NOT_ENOUGH).call()
            return ExecuteResult.MANA_NOT_ENOUGH
        }

        val wg = !WorldGuardHook.cast(player)
        if (wg) {
            return ExecuteResult.WorldGuardPVP
        }

        Counting.reset(player, session)
        takeMana(mana)
        PlayerCastSkillEvents.Record(player, skill).call()

        session.cast()
        session.closed = true

        PlayerCastSkillEvents.Post(player, skill).call()
        return ExecuteResult.SUCCESS
    }

    fun getSkill(skillName: String): Skill? {
        return skills.firstOrNull { it.key == skillName }
    }

    fun regSkill(file: File): Skill {
        val skill = Skill(file.toYamlName(), Configuration.loadFromFile(file))
        skills += skill
        ScriptLoader.autoLoad()
        return skill
    }

    fun checkUpgrade(player: Player, playerSkill: PlayerJob.Skill): Boolean {
        val conditions = getUpgradeConditions(playerSkill)
        return conditions.isEmpty() || conditions.any { return checkCondition(player, playerSkill, it) }
    }

    fun tryUpgrade(player: Player, playerSkill: PlayerJob.Skill): CompletableFuture<UpgradeResult> {

        val profile = player.plannersProfile

        // 优先判定技能点
        return playerSkill.getNeedPoints(player).thenApply { points ->
            if (profile.point < points) {
                player.sendLang("player-points-law", points, profile.point)
                return@thenApply UpgradeResult.LAW_POINTS
            }

            // 如果满级 则失败
            if (playerSkill.isMax) return@thenApply UpgradeResult.MAX_LEVEL

            // 如果不满足条件 则失败
            if (!checkUpgrade(player, playerSkill)) return@thenApply UpgradeResult.MATCH_CONDITION

            val context = Context.Impl(player.toTarget(), playerSkill.instance)

            getUpgradeConditions(playerSkill).forEach {
                it.consumeTo(context)
            }
            // 优先扣除技能点
            profile.addPoint(-points)
            profile.add(playerSkill, 1)
            return@thenApply UpgradeResult.SUCCESS
        }

    }

    fun resetSkillPoint(profile: PlayerProfile, skill: PlayerJob.Skill) {
        if (PlayerSkillResetEvent.Pre(profile, skill).call()) {
            skill.level = 0
            PlayerSkillResetEvent.Post(profile, skill).call()
            submitAsync {
                Storage.INSTANCE.updateSkill(profile, skill)
            }
        }

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
        return checkCondition(Context.Impl(player.toTarget(), playerSkill.instance), it)
    }

    fun checkCondition(player: Player, condition: Condition): Boolean {
        return runKether {
            ScriptLoader.createScript(ContextAPI.create(player), condition.condition) {

            }.thenApply { Coerce.toBoolean(it) }.get()
        } ?: false
    }

    fun checkCondition(context: Context.Impl, condition: Condition): Boolean {
        return runKetherThrow(context.id) {
            ScriptLoader.createScript(context, condition.condition).get().cbool
        } ?: false
    }

    fun getKeySlot(id: String): IKeySlot? {
        return keySlots.firstOrNull { it.key == id }
    }

    fun hasJob(key: String) = key in jobs.map { it.key }

    fun getJob(key: String) = jobs.firstOrNull { it.key == key } ?: error("Job '${key}' not found.")

    fun getRouterStartJob(router: Router): Job {
        return jobs.firstOrNull { it.key == router.start } ?: error("Job '${router.start}' not found.")
    }


    fun PlayerProfile.hasCast(skill: Skill): Boolean {
        return (getSkill(skill.key)?.level ?: 0) > 0
    }

    fun PlayerProfile.checkCast(skill: Skill): ExecuteResult {

        if (!hasCast(skill)) {
            return ExecuteResult.LEVEL_ZERO
        }

        val session = ContextAPI.createSession(player, skill)

        if (!Counting.hasNext(player, skill)) {
            return ExecuteResult.COOLING
        }

        val mana = Coerce.toDouble(session.mpCost.get())
        if (toCurrentMana() < mana) {
            return ExecuteResult.MANA_NOT_ENOUGH
        }

        return ExecuteResult.SUCCESS

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
