package com.bh.planners.api.enums

import com.bh.planners.api.counter.Counting
import com.bh.planners.core.pojo.Skill
import com.bh.planners.core.pojo.player.PlayerProfile
import org.bukkit.entity.Player
import taboolib.platform.util.sendLang

enum class ExecuteResult {

    WAITING {
        override val handler: Player.(Skill) -> Unit
            get() = {

            }
    },
    COOLING {
        override val handler: Player.(Skill) -> Unit
            get() = { skill ->
                sendLang("skill-cast-cooling", skill.option.name, Counting.getCountdown(this, skill) / 1000)
            }
    },
    SUCCESS {
        override val handler: Player.(Skill) -> Unit
            get() = {

            }
    },
    MANA_NOT_ENOUGH {
        override val handler: Player.(Skill) -> Unit
            get() = { skill ->
                sendLang("skill-cast-mana-not-enough", skill.option.name)
            }
    },
    LEVEL_ZERO {
        override val handler: Player.(Skill) -> Unit
            get() = { skill ->
                sendLang("skill-cast-level-zero", skill.option.name)
            }
    };

    abstract val handler: Player.(Skill) -> Unit

}