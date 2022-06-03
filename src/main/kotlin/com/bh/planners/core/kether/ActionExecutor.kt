package com.bh.planners.core.kether

import org.bukkit.Location
import org.bukkit.entity.Player
import taboolib.module.kether.*

class ActionExecutor {


    companion object {

        /**
         * 释放者位置 #仅可在玩家释放技能里食用
         * executor loc
         * 释放者uuid #仅可在玩家释放技能里食用
         * executor uuid
         */
        @KetherParser(["executor"], namespace = NAMESPACE)
        fun parser() = scriptParser {

            it.switch {
                case("loc") {
                    actionNow { script().sender!!.cast<Player>().location.toLocal() }
                }
                case("uuid") {
                    actionNow { script().sender!!.cast<Player>().uniqueId.toString() }
                }
            }

        }

        fun Location.toLocal(): String {
            return "${world!!.name},$x,$y,$z"
        }

    }

}