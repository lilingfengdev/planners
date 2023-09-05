package com.bh.planners.core.kether.game.item

import com.bh.planners.core.kether.NAMESPACE
import com.bh.planners.core.kether.nextOptionalParsedAction
import com.bh.planners.core.kether.nextSelectorOrNull
import taboolib.module.kether.KetherParser
import taboolib.module.kether.expects
import taboolib.module.kether.scriptParser
import taboolib.module.kether.switch

object ItemOperator {

    /**
     *
     * 给目标物品增加某个关键字的计数操作
     * item count add [keyword: action] [amount: action] <max: action(INT_MAX)> <they selector>
     * 给目标物品设置某个关键字的计数操作
     * item count set [keyword: action] [amount: action] <they selector>
     * 获取目标物品某个关键字的计数
     * item count get [keyword: action] <they selector>
     */
    @KetherParser(["item"], namespace = NAMESPACE, shared = true)
    fun parser() = scriptParser {
        it.switch {
            case("count") {
                when (it.expects("add", "get", "set")) {
                    "add" -> ActionItemCountAdd(
                        it.nextParsedAction(),
                        it.nextParsedAction(),
                        it.nextParsedAction(),
                        it.nextOptionalParsedAction(arrayOf("max", "maximum"), Int.MAX_VALUE)!!,
                        it.nextSelectorOrNull()
                    )

                    "get" -> ActionItemCountGet(it.nextParsedAction(), it.nextParsedAction(), it.nextSelectorOrNull())

                    "set" -> ActionItemCountSet(
                        it.nextParsedAction(),
                        it.nextParsedAction(),
                        it.nextParsedAction(),
                        it.nextSelectorOrNull()
                    )

                    else -> error("error of case!")
                }
            }

            case("name", "display") {
                ActionItemName(it.nextParsedAction(), it.nextSelectorOrNull())
            }

            case("lore") {
                ActionItemLore(it.nextParsedAction(), it.nextSelectorOrNull())
            }
        }
    }

    val FILTER_RULES = listOf(
        Regex("§+[a-z0-9%]"),
        Regex("[^0-9+--.]"),
    )

    fun getNumber(string: String): String {
        var prey = string
        FILTER_RULES.forEach { prey = prey.replace(it, "") }
        return prey.ifEmpty { "0.0" }
    }
}