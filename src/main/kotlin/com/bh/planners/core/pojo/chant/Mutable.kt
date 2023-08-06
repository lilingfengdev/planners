package com.bh.planners.core.pojo.chant

import org.bukkit.entity.Player
import taboolib.common.util.replaceWithOrder
import taboolib.common5.cint
import java.lang.StringBuilder

interface Mutable<T> {

    fun build(value: Double): T

    // mutable text "星火燎原 引导:{0}" space "" step 10 with "&a-" fill "&7"
    class Text(val message: String, val join: String, val fill: String, val space: String, val step: Int) : Mutable<String> {

        override fun build(value: Double): String {
            val texts = Array(step) { fill }
            val amount = (value * step).cint
            repeat(amount) { index ->
                texts[index] = join
            }
            return message.replaceWithOrder(texts.joinToString(space))
        }

    }

}