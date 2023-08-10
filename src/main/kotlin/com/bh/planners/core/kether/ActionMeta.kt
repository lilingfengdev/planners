package com.bh.planners.core.kether

import com.bh.planners.api.Counting
import com.bh.planners.api.maxLevel
import com.bh.planners.api.optAsync
import com.bh.planners.core.effect.Target.Companion.getEntity
import com.bh.planners.core.kether.meta.ActionMetaOrigin
import com.bh.planners.core.module.mana.ManaManager
import taboolib.module.kether.*

class ActionMeta {


    companion object {

        /**
         * meta executor name
         * meta executor uuid
         * meta executor loc
         * meta executor mana
         * meta executor mana max
         *
         * meta skill name
         * meta skill async
         * meta skill level
         * meta skill level cap
         *
         * meta origin
         */
        @KetherParser(["meta"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            it.switch {

                case("skill") {
                    when (expects("id", "name", "async", "level", "level-cap", "level-max", "shortcut", "countdown")) {
                        "id" -> actionSkillNow { it.key }
                        "name" -> actionSkillNow { it.name }
                        "async" -> actionSkillNow { it.optAsync }
                        "level" -> actionSkillNow { it.level }
                        "level-cap", "level-max", "maxlevel", "max-level" -> actionSkillNow { it.maxLevel }
                        "shortcut" -> actionSkillNow { it.keySlot?.name ?: "暂无" }
                        "countdown" -> actionSkillNow {
                            Counting.getCountdown(
                                bukkitPlayer() ?: return@actionSkillNow -1, it.instance
                            )
                        }

                        else -> actionNow { "error" }
                    }

                }

                case("executor") {
                    when (expects("name", "uuid", "loc", "location", "mana", "max-mana")) {
                        "name" -> actionNow { executor().name }
                        "uuid" -> actionNow { bukkitTarget().getEntity()?.uniqueId }
                        "loc", "location" -> actionNow { bukkitTarget().getEntity()?.location?.clone() }
                        "mana" -> actionProfileNow { ManaManager.INSTANCE.getMana(it) }
                        "max-mana", "maxmana" -> actionProfileNow { ManaManager.INSTANCE.getMaxMana(it) }
                        else -> actionNow { "error" }
                    }
                }
                case("origin") {
                    try {
                        mark()
                        expects("to", "set", "=", "bind")
                        try {
                            it.mark()
                            expects("they", "the")
                            ActionMetaOrigin.Set(it.nextParsedAction())
                        } catch (_: Throwable) {
                            it.reset()
                            ActionMetaOrigin.Set(it.nextParsedAction())
                        }

                    } catch (e: Throwable) {
                        reset()
                        actionNow { origin() }
                    }
                }
            }
        }

    }

}