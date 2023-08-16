package com.bh.planners.core.feature.damageable

import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.kether.rootVariables
import com.bh.planners.core.pojo.Context
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.script

fun ScriptFrame.setDamageable(damageable: Damageable) {
    this.rootVariables()["damageable"] = damageable
    this.rootVariables()["model"] = damageable
    this.rootVariables()["@DamageableContext"] = damageable
}

fun ScriptFrame.getDamageable(): Damageable {
    return this.rootVariables().get<Damageable>("@DamageableContext").orElse(null)
        ?: error("Error running environment !")
}

fun ScriptFrame.setDamageableMeta(meta: DamageableMeta) {
    this.rootVariables()["@DamageableMeta"] = meta
}

fun ScriptFrame.setSender(entity: Entity) {
    if (entity is Player) {
        this.script().sender = adaptPlayer(entity)
    }
    this.variables()["@context"] = object : Context(entity.toTarget()) {}
}

fun ScriptFrame.getDamageableMeta(): DamageableMeta {
    return rootVariables().get<DamageableMeta>("@DamageableMeta").orElse(null) ?: error("Error running environment !")
}
