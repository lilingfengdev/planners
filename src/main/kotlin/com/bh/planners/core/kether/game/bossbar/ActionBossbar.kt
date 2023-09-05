package com.bh.planners.core.kether.game.bossbar

import com.bh.planners.core.kether.NAMESPACE
import com.bh.planners.core.kether.nextOptionalParsedAction
import taboolib.module.kether.KetherParser
import taboolib.module.kether.scriptParser
import taboolib.module.kether.switch

object ActionBossbar {

    /**
     * 创建一个bossbar 并且在timeout/tick后删除 -1为不删除 将返回一个bossbar的id用于后续操作
     * bossbar create [id: string] <timeout: action>
     *
     * 删除bossbar
     * bossbar delete [id: string]
     *
     * 修改bossbar的标题
     * bossbar title [id: string] [value: action]
     *
     * 修改bossbar的style样式
     * bossbar style [id: action] [style: action]
     *
     * 修改bossbar的颜色
     * bossbar color [id: action] [color: action]
     *
     * 添加一项bossbar的flag标签
     * bossbar flag add [flag: action]
     *
     * 删除一项bossbar的flag标签
     * bossbar flag remove [flag: action]
     *
     * 设置bossbar的进度
     * bossbar progress [id: action] [value: action]
     *
     */
    @KetherParser(["bossbar"], namespace = NAMESPACE, shared = true)
    fun parser() = scriptParser {
        it.switch {

            case("create") {
                ActionBossBarCreate(it.nextParsedAction(),it.nextOptionalParsedAction(arrayOf("tick", "timeout"), -1)!!)
            }

            case("delete") {
                ActionBossBarDelete(it.nextParsedAction())
            }

            case("title") {
                ActionBossBarTitle(it.nextParsedAction(), it.nextParsedAction())
            }

            case("style") {
                ActionBossBarStyle(it.nextParsedAction(), it.nextParsedAction())
            }

            case("color") {
                ActionBossBarColor(it.nextParsedAction(), it.nextParsedAction())
            }

//            case("viewer") {
//                when(nextToken()) {
//                    "add" ->
//                }
//            }

        }
    }


}