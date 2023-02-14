package com.bh.planners.core.kether.compat.germplugin

import com.germ.germplugin.api.GermPacketAPI
import com.germ.germplugin.api.dynamic.effect.EffectManager
import com.germ.germplugin.api.dynamic.effect.GermEffectPart
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.run
import taboolib.platform.util.onlinePlayers
import java.util.concurrent.CompletableFuture

/**
 * 取消来自（萌芽特效）
 * germ stop <action(effect|?)>
 */
class ActionGermStop(val action: ParsedAction<*>) : ScriptAction<Void>() {
    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        return frame.run(action).thenAccept {
            if (it is GermEffectPart<*>) {
                onlinePlayers.forEach { player -> GermPacketAPI.removeEffect(player, it.indexName) }
            }
        }
    }


}