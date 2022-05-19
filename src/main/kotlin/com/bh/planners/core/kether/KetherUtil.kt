package com.bh.planners.core.kether

import com.bh.planners.core.pojo.Session
import com.bh.planners.util.StringNumber
import taboolib.module.kether.ScriptFrame

const val NAMESPACE = "Planners"

val namespaces = listOf(NAMESPACE)


fun ScriptFrame.getSession(): Session {
    return variables().get<Session>("@PlannersSession").orElse(null) ?: error("Error running environment !")
}


fun Any?.increaseAny(any: Any): Any {
    this ?: return any
    return StringNumber(toString()).add(any.toString()).get()
}
