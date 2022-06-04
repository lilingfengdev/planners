package com.bh.planners.core.kether

import com.bh.planners.api.ManaCounter.toCurrentMana
import com.bh.planners.api.ManaCounter.toMaxMana
import com.bh.planners.core.kether.meta.ActionMetaOrigin
import taboolib.library.kether.ArgTypes
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
        @KetherParser(["meta"], namespace = NAMESPACE)
        fun parser() = scriptParser {
            it.switch {

                case("skill") {
                    when (expects("name", "async", "level")) {
                        "name" -> actionNow { skill().instance.option.name }
                        "async" -> actionNow { skill().instance.option.async }
                        "level" -> actionNow { skill().level }
                        "level-cap", "level-max" -> actionNow { skill().instance.option.levelCap }
                        else -> actionNow { "error" }
                    }

                }

                case("executor") {
                    when (expects("name", "uuid", "loc", "location")) {
                        "name" -> actionNow { executor().name }
                        "uuid" -> actionNow { asPlayer().uniqueId.toString() }
                        "loc", "location" -> actionNow { asPlayer().location.toLocal() }
                        "mana" -> actionNow { asPlayer().toCurrentMana() }
                        "max-mana" -> actionNow { asPlayer().toMaxMana() }
                        else -> actionNow { "error" }
                    }
                }
                case("origin") {
                    try {
                        mark()
                        expects("set", "to")
                        ActionMetaOrigin.Set(it.next(ArgTypes.ACTION))
                    } catch (_: Throwable) {
                        reset()
                        actionNow { toOriginLocation()?.value ?: "__empty__" }
                    }
                }
            }
        }

    }

}