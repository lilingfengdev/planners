package com.bh.planners.core.feature.damageable

class DamageableMeta(val context: Damageable, val stream: DamageableModel.Stream) {


    val type = stream.type

    var sender = if (type == DamageableModel.Type.ATTACK) context.attacker else context.victim

    var data: Any? = null

    var cancelStream = false


    fun changeSender() {
        if (sender == context.attacker) {
            sender = context.victim
        } else {
            sender = context.attacker
        }
    }

}