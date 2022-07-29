package com.bh.planners.core.kether.compat.germplugin

import com.bh.planners.core.kether.execLocation
import com.bh.planners.core.kether.runTransfer
import com.germ.germplugin.api.GermPacketAPI
import com.germ.germplugin.api.SoundType
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import java.util.concurrent.CompletableFuture

class ActionGermSound(val name: ParsedAction<*>, val type: ParsedAction<*>, val volume: ParsedAction<*>, val pitch: ParsedAction<*>, val selector: ParsedAction<*>) : ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        frame.runTransfer<String>(name) { name ->
            frame.runTransfer<SoundType>(type) { type ->
                frame.runTransfer<Float>(volume) { volume ->
                    frame.runTransfer<Float>(pitch) { pitch ->
                        frame.execLocation(selector) {
                            GermPacketAPI.playSound(this, name, type, 0, volume, pitch)
                        }
                    }
                }
            }
        }

        return CompletableFuture.completedFuture(null)
    }
}