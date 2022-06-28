package com.bh.planners.core.kether

import com.bh.planners.core.skill.effect.Target
import com.bh.planners.core.kether.selector.Fetch.asContainer
import com.bh.planners.core.pojo.Context
import com.bh.planners.core.pojo.Session
import com.bh.planners.core.pojo.data.Data
import com.bh.planners.core.pojo.data.DataContainer.Companion.unsafeData
import org.bukkit.Location
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionTarget {


    class ActionTargetSwitch(val action: ParsedAction<*>) : ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            return frame.newFrame(action).run<Any>().thenAccept {
                frame.getSession().flags["@Target"] = (it as? Target)?.unsafeData() ?: return@thenAccept
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
            return getSession().getTarget()
        }

        fun Context.getTarget(): Target? {
            return flags["@Target"]?.asTarget()
        }

        @KetherParser(["target"], namespace = NAMESPACE)
        fun parser() = scriptParser {
            it.switch {
                case("switch") {
                    ActionTargetSwitch(it.next(ArgTypes.ACTION))
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