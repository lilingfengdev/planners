package com.bh.planners.core.kether

import org.bukkit.entity.Player
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.library.kether.actions.LiteralAction
import taboolib.module.kether.*
import java.util.*
import java.util.concurrent.CompletableFuture

class ActionUUID {


    class RandomUUID : ScriptAction<String>() {
        override fun run(frame: ScriptFrame): CompletableFuture<String> {
            return CompletableFuture.completedFuture(UUID.randomUUID().toString())
        }
    }

    companion object {

        @KetherParser(["uuid"])
        fun parser() = scriptParser {
            it.switch {
                case("random") {
                    RandomUUID()
                }
                case("player") {
                    actionNow {
                        script().sender!!.cast<Player>().uniqueId.toString()
                    }
                }
            }
        }

    }

}