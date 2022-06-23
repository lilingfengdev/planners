package com.bh.planners.core.skill.effect

import com.bh.planners.core.skill.effect.renderer.EffectRenderer
import com.bh.planners.core.kether.getSession
import com.bh.planners.core.kether.toOriginLocation
import com.bh.planners.core.pojo.Session
import org.bukkit.Location
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import java.util.concurrent.CompletableFuture

abstract class Effect {

    abstract val name : String

    abstract fun sendTo(target: Target?, option: EffectOption, session: Session)


}
