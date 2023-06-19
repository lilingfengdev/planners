package com.bh.planners.core.kether.compat.mythicmobs

import com.bh.planners.core.kether.*
import taboolib.module.kether.KetherParser
import taboolib.module.kether.scriptParser
import taboolib.module.kether.switch

object ActionMythicMobsLoader {


    /**
     * 给目标发送mm信号
     * mythic signal [name: String] [they selector]
     *
     * 给玩家目标释放MM技能
     * mythic cast [name: String] [power: Float] [they selector]
     *
     */
    @KetherParser(["mm", "mythic", "mythicmobs"], namespace = NAMESPACE, shared = true)
    fun parser() = scriptParser {
        it.switch {
            case("signal") {
                ActionMythicSignal(it.nextToken(), it.nextSelectorOrNull())
            }
            case("cast") {
                ActionMythicCast(it.nextToken(), it.nextToken().toFloat(), it.nextSelectorOrNull())
            }
        }
    }

}