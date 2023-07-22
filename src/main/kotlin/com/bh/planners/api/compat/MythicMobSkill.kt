package com.bh.planners.api.compat

import com.bh.planners.api.PlannersAPI
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.pojo.Context
import io.lumine.xikage.mythicmobs.io.MythicLineConfig
import io.lumine.xikage.mythicmobs.skills.INoTargetSkill
import io.lumine.xikage.mythicmobs.skills.SkillMechanic
import io.lumine.xikage.mythicmobs.skills.SkillMetadata
import io.lumine.xikage.mythicmobs.skills.placeholders.parsers.PlaceholderInt
import io.lumine.xikage.mythicmobs.skills.placeholders.parsers.PlaceholderString
import taboolib.common.platform.function.warning

class MythicMobSkill(line: String, config: MythicLineConfig) : SkillMechanic(line, config), INoTargetSkill {

    val id: PlaceholderString = config.getPlaceholderString(arrayOf("id", "skill", "s"), "__NULL__")
    val level: PlaceholderInt = config.getPlaceholderInteger(arrayOf("level", "l"), 1)

    override fun cast(data: SkillMetadata): Boolean {
        val skillId = id.get()
        if (skillId == "__NULL__") {
            return false
        }
        val skill = PlannersAPI.getSkill(skillId)
        if (skill == null) {
            warning("Skill $skillId not found")
            return false
        }
        val entity = data.caster.entity.bukkitEntity
        Context.Impl1(entity.toTarget(), skill, level.get()).cast()
        return true
    }

}