package com.bh.planners.core.kether

import com.bh.planners.api.ManaCounter.toCurrentMana
import com.bh.planners.api.ManaCounter.toMaxMana
import org.bukkit.entity.Player
import taboolib.module.kether.*

class ActionPlanners {


    companion object {

        /**
         * 法力
         * profile mana
         *
         * 最大法力
         * profile maxxMana
         */
        @KetherParser(["profile"], namespace = NAMESPACE)
        fun parser() = scriptParser {
            it.switch {
                case("mana") {
                    actionNow {
                        script().sender!!.cast<Player>().toCurrentMana()
                    }
                }
                case("maxMana") {
                    actionNow {
                        script().sender!!.cast<Player>().toMaxMana()
                    }
                }
            }
        }

    }


}
