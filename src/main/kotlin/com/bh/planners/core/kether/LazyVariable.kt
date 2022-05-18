package com.bh.planners.core.kether

import com.bh.planners.core.pojo.Session
import com.bh.planners.core.pojo.Skill
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.kether.KetherFunction

class LazyVariable(val variable: Skill.Variable, val session: Session) {


    override fun toString(): String {
        return KetherFunction.parse(variable.expression, namespace = namespaces, sender = adaptPlayer(session.executor))
    }

}
