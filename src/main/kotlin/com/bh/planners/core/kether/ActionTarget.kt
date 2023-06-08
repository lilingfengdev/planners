package com.bh.planners.core.kether

import com.bh.planners.core.effect.Target
import com.bh.planners.core.pojo.data.Data
import com.bh.planners.core.pojo.data.DataContainer.Companion.unsafeData
import org.bukkit.Location
import taboolib.common.util.orNull
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionTarget {


    class ActionTargetSwitch(val action: ParsedAction<*>) : ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            return frame.newFrame(action).run<Any>().thenAccept {
                frame.variables()["@Target"] = (it as? Target)?.unsafeData()
            }
        }
    }

    class ActionTargetGet : ScriptAction<Target?>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Target?> {
            return CompletableFuture.completedFuture(frame.getTarget())
        }
    }

    class ActionTargetLocation() : ScriptAction<Location>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Location> {
            return CompletableFuture.completedFuture(frame.getTarget()?.toLocation())
        }
    }

    companion object {

        fun Data.asTarget(): Target {
            return data as Target
        }

        fun ScriptFrame.getTarget(): Target? {
            return variables().get<Target>("@Target").orNull()
        }


        @KetherParser(["target"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            it.switch {
                case("switch") {
                    ActionTargetSwitch(it.nextParsedAction())
                }
                case("get") {
                    ActionTargetGet()
                }
                case("location") {
                    ActionTargetLocation()
                }
                other {
                    ActionTargetGet()
                }
            }
        }

    }

}