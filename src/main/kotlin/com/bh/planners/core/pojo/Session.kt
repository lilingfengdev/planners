package com.bh.planners.core.pojo

import com.bh.planners.api.PlannersAPI.profile
import com.bh.planners.core.kether.namespaces
import com.bh.planners.core.pojo.player.PlayerJob
import com.bh.planners.core.pojo.player.PlayerProfile
import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.kether.KetherFunction
import taboolib.module.kether.KetherShell
import taboolib.module.kether.printKetherErrorMessage

class Session(val executor: Player, val skill: Skill) {

    val profile: PlayerProfile
        get() = executor.profile()

    val playerSkill: PlayerJob.Skill
        get() = profile.job!!.getSkill(skill.key)

    val cache = mutableMapOf<String, Any>()

    fun getLazyVariable(key: String): Any {
        if (cache.containsKey(key)) {
            return cache[key].toString()
        }
        val variable =
            playerSkill.instance.option.variables.firstOrNull { it.key == key } ?: error("$key skill not found.")
        cache[key] = KetherFunction.parse(variable.expression, namespace = namespaces, sender = adaptPlayer(executor)) {
            this["@Session"] = this@Session
            this["@Skill"] = skill
        }
        return getLazyVariable(key)
    }

    fun cast() {
        try {
            KetherShell.eval(skill.action, sender = adaptPlayer(executor), namespace = namespaces) {
                this["@Session"] = this@Session
                this["@Skill"] = skill
            }
        } catch (e: Throwable) {
            e.printKetherErrorMessage()
        }
    }
}
