package com.bh.planners.core.kether

import com.bh.planners.api.event.proxy.ProxyDamageEvent
import com.bh.planners.core.kether.common.CombinationKetherParser
import com.bh.planners.core.kether.common.MultipleKetherParser
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import taboolib.module.kether.*

@CombinationKetherParser.Used
object ActionEvent : MultipleKetherParser("event") {

    fun ScriptFrame.event(): Event {
        return rootVariables().get<Event>("@Event").orElse(null) ?: error("Error running environment !")
    }

    val cancel = combinationParser {
        it.group(command("to", then = bool()).option().defaultsTo(true)).apply(it) { value ->
            now {
                (event() as? Cancellable)?.isCancelled = value
            }
        }
    }

    val damage = scriptParser {
        it.switch {
            case("set", "to") {
                val action = it.nextParsedAction()
                actionNow {
                    run(action).double { value ->
                        (event() as? ProxyDamageEvent)?.damage = value
                    }

                }
            }

            case("add") {
                val action = it.nextParsedAction()
                actionNow {
                    run(action).double { value ->
                        (event() as? ProxyDamageEvent)?.addDamage(value)
                    }

                }
            }

            case("take") {
                val action = it.nextParsedAction()
                actionNow {
                    run(action).double { value ->
                        (event() as? ProxyDamageEvent)?.addDamage(-value)
                    }

                }
            }

            other { actionNow { (event() as? ProxyDamageEvent)?.damage } }
        }
    }


}