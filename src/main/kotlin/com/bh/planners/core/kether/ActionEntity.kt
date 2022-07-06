package com.bh.planners.core.kether

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.LivingEntity
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.*
import java.util.concurrent.CompletableFuture

class ActionEntity {

    class OfEntity(val action: ParsedAction<*>) : ScriptAction<LivingEntity>() {
        override fun run(frame: ScriptFrame): CompletableFuture<LivingEntity> {
            return frame.newFrame(action).run<Any>().thenApply {
                Bukkit.getEntity(UUID.fromString(it.toString())) as LivingEntity
            }
        }

    }

    class LocationGet(val action: ParsedAction<*>) : ScriptAction<Location>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Location> {
            return frame.newFrame(action).run<Any>().thenApply {
                (Bukkit.getEntity(UUID.fromString(it.toString())) as LivingEntity).location
            }
        }
    }


    companion object {

        /**
         * entity of [uuid: action]
         * entity loc [entity : action]
         */
        @KetherParser(["entity"])
        fun parser() = scriptParser {
            it.switch {
                case("of") {
                    OfEntity(it.next(ArgTypes.ACTION))
                }
                case("loc", "location") {
                    LocationGet(it.next(ArgTypes.ACTION))
                }
            }

        }

    }

}