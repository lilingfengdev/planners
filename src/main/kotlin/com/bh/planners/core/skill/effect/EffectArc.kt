package com.bh.planners.core.skill.effect

import com.bh.planners.core.skill.effect.Target.Companion.createContainer
import com.bh.planners.core.skill.effect.renderer.ArcRenderer
import com.bh.planners.core.skill.effect.renderer.EffectRenderer
import com.bh.planners.core.pojo.Session
import com.bh.planners.core.skill.effect.common.PlayerFrontCoordinate
import io.lumine.utils.Players.spawnParticle
import taboolib.common.platform.function.submit
import taboolib.common5.Coerce
import taboolib.library.kether.ParsedAction
import kotlin.math.cos
import kotlin.math.sin

object EffectArc : Effect() {

    override val name: String
        get() = "arc"

    override fun sendTo(target: Target?, option: EffectOption, session: Session) {
        ArcRenderer(target!!, option.createContainer(target, session), option).sendTo()
    }

}
