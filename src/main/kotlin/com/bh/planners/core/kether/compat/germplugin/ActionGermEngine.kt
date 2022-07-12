package com.bh.planners.core.kether.compat.germplugin

import com.bh.planners.core.effect.Target
import com.bh.planners.core.kether.*
import com.bh.planners.core.kether.compat.adyeshach.AdyeshachEntity
import com.germ.germplugin.api.GermPacketAPI
import com.germ.germplugin.api.SoundType
import com.germ.germplugin.api.dynamic.effect.GermEffectParticle
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.manager.Manager
import ink.ptms.adyeshach.common.script.ScriptHandler.getEntities
import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.UUID
import java.util.concurrent.CompletableFuture

class ActionGermEngine {

    class ActionAnimation(val state: String, val remove: Boolean, val selector: ParsedAction<*>) :
        ScriptAction<Void>() {

        fun execute(entity: Entity, state: String, remove: Boolean) {
            Bukkit.getOnlinePlayers().forEach {
                if (remove) {
                    GermPacketAPI.stopModelAnimation(it, entity.entityId, state)
                } else {
                    GermPacketAPI.sendModelAnimation(it, entity.entityId, state)
                }
            }
        }

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            frame.execEntity(selector) { execute(this, state, remove) }
            return CompletableFuture.completedFuture(null)
        }
    }

    class ActionSound(
        val name: ParsedAction<*>,
        val type: ParsedAction<*>,
        val volume: ParsedAction<*>,
        val pitch: ParsedAction<*>,
        val selector: ParsedAction<*>
    ) :
        ScriptAction<Void>() {


        override fun run(frame: ScriptFrame): CompletableFuture<Void> {

            frame.transfer<String>(name) { name ->
                frame.transfer<SoundType>(type) { type ->
                    frame.transfer<Float>(volume) { volume ->
                        frame.transfer<Float>(pitch) { pitch ->
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

    class ActionParticle(val path: ParsedAction<*>, val selector: ParsedAction<*>?) : ScriptAction<UUID>() {

        fun execute(target: Target, effect: GermEffectParticle) {
            Bukkit.getOnlinePlayers().forEach {
                if (target is Target.Entity) {
                    effect.spawnToEntity(it, target.entity)
                } else if (target is Target.Location) {
                    effect.spawnToLocation(it, target.value)
                }
            }
        }

        override fun run(frame: ScriptFrame): CompletableFuture<UUID> {
            val future = CompletableFuture<UUID>()
            val randomUUID = UUID.randomUUID()
            frame.transfer<String>(path) {
                val effectParticle = GermEffectParticle(randomUUID.toString())
                effectParticle.path = it
                if (selector != null) {
                    frame.createContainer(selector).thenAccept {
                        it.targets.forEach { execute(it, effectParticle) }
                    }
                } else {
                    execute(frame.toOriginLocation()!!, effectParticle)
                }
            }

            return future
        }


    }

    companion object {

        /**
         * germ animation send [name: token] [selector]
         * germ animation stop [name: token] [selector]
         *
         * germ sound name <soundtype> <volume> <pitch> <selector>
         *
         * germ sound name soundtype master volume 1.0 pitch 1.0 they "-@self"
         *
         * germ particle [path: action] <selector>
         */
        @KetherParser(["germengine", "germ", "germplugin"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            it.switch {
                case("animation") {
                    when (it.expects("send", "stop")) {
                        "send" -> {
                            ActionAnimation(it.nextToken(), false, it.selector())
                        }
                        "stop" -> {
                            ActionAnimation(it.nextToken(), true, it.selector())
                        }
                        else -> error("out of case")
                    }
                }
                case("sound") {
                    ActionSound(
                        it.next(ArgTypes.ACTION),
                        it.tryGet(arrayOf("soundtype", "type"), "MASTER")!!,
                        it.tryGet(arrayOf("volume"), 1.0f)!!,
                        it.tryGet(arrayOf("pitch"), 1.0f)!!,
                        it.selector()
                    )
                }
                case("snowstom") {
                    ActionParticle(it.next(ArgTypes.ACTION),it.selectorAction())
                }
            }
        }
    }
}