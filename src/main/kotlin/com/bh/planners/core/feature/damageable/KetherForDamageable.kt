package com.bh.planners.core.feature.damageable

import com.bh.planners.core.kether.rootVariables
import com.bh.planners.core.pojo.Context
import taboolib.module.kether.ScriptContext
import taboolib.module.kether.ScriptFrame

fun ScriptContext.setDamageable(damageable: Damageable) {
    this.rootFrame().rootVariables()["@DamageableContext"] = damageable
}

fun ScriptContext.getDamageable(): Damageable {
    return this.rootFrame().get<Damageable>("@DamageableContext").orElse(null) ?: error("Error running environment !")
}

fun ScriptContext.setDamageableMeta(meta: DamageableMeta) {
    this.rootFrame().rootVariables()["@DamageableMeta"] = meta
}

fun ScriptContext.getDamageableMeta(): Damageable {
    return rootFrame().get<DamageableMeta>("@DamageableMeta").orElse(null) ?: error("Error running environment !")
}
