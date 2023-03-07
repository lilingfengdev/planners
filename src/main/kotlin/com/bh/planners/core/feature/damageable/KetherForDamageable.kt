package com.bh.planners.core.feature.damageable

import com.bh.planners.core.kether.rootVariables
import com.bh.planners.core.pojo.Context
import taboolib.module.kether.ScriptContext
import taboolib.module.kether.ScriptFrame

fun ScriptFrame.setDamageable(damageable: Damageable) {
    this.rootVariables()["damageable"] = damageable
    this.rootVariables()["model"] = damageable
    this.rootVariables()["@DamageableContext"] = damageable
}

fun ScriptFrame.getDamageable(): Damageable {
    return this.rootVariables().get<Damageable>("@DamageableContext").orElse(null) ?: error("Error running environment !")
}

fun ScriptFrame.setDamageableMeta(meta: DamageableMeta) {
    this.rootVariables()["@DamageableMeta"] = meta
}
fun ScriptFrame.getDamageableMeta(): DamageableMeta {
    return rootVariables().get<DamageableMeta>("@DamageableMeta").orElse(null) ?: error("Error running environment !")
}
