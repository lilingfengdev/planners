package com.bh.planners.core.pojo.chant

import com.bh.planners.core.pojo.Session
import org.bukkit.entity.Player

interface Process {

    val sender: Player

    // 中断结束标识
    var actionBreak: Boolean


    class Default(override val sender: Player, val session: Session, val tags: List<Interrupt>) : Process {

        override var actionBreak = false

    }

}