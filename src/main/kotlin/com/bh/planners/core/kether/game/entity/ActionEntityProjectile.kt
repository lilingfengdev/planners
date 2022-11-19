package com.bh.planners.core.kether.game.entity

import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.inline.Incident.Companion.handleIncident
import com.bh.planners.core.effect.inline.IncidentCaptureEntity
import com.bh.planners.core.effect.inline.IncidentHitEntity
import com.bh.planners.core.effect.rotateAroundX
import com.bh.planners.core.effect.rotateAroundY
import com.bh.planners.core.effect.rotateAroundZ
import com.bh.planners.core.kether.*
import com.bh.planners.core.pojo.Context
import com.bh.planners.core.pojo.Session
import org.bukkit.entity.*
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.metadata.FixedMetadataValue
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.KetherParser
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.scriptParser
import taboolib.platform.BukkitPlugin
import java.util.concurrent.CompletableFuture

class ActionEntityProjectile {

    class ActionLaunch(
        val action: ParsedAction<*>,
        val name: ParsedAction<*>,
        val step: ParsedAction<*>,
        val rotateX: ParsedAction<*>,
        val rotateY: ParsedAction<*>,
        val rotateZ: ParsedAction<*>,
        val event: ParsedAction<*>,
        val selector: ParsedAction<*>?,
    ) :
        ScriptAction<List<Entity>>() {

        fun execute(context: Context, name: String, owners: List<LivingEntity>, type: Type, step: Double, event: String, rotateX: Double, rotateY: Double, rotateZ: Double, ): List<Projectile> {
            val listOf = mutableListOf<Projectile>()
            owners.forEach {
                val projectile = it.launchProjectile(type.clazz)
                if (event != "none") {
                    projectile.setMetadata("owner", FixedMetadataValue(BukkitPlugin.getInstance(), it))
                    projectile.setMetadata("event", FixedMetadataValue(BukkitPlugin.getInstance(), event))
                    projectile.setMetadata("context", FixedMetadataValue(BukkitPlugin.getInstance(), context))
                }

                projectile.customName = name
                projectile.isCustomNameVisible = false
                val velocity = projectile.velocity

                // 处理向量旋转
                rotateAroundX(velocity, rotateX)
                rotateAroundY(velocity, rotateY)
                rotateAroundZ(velocity, rotateZ)

                projectile.velocity = velocity.multiply(step);
                listOf += projectile
            }
            return listOf
        }

        override fun run(frame: ScriptFrame): CompletableFuture<List<Entity>> {

            val future = CompletableFuture<List<Entity>>()
            frame.runTransfer0<Type>(action) { type ->
                frame.runTransfer0<String>(name) { name ->
                    frame.runTransfer0<Double>(step) { step ->
                        frame.runTransfer0<Double>(rotateX) { rotateX ->
                            frame.runTransfer0<Double>(rotateY) { rotateY ->
                                frame.runTransfer0<Double>(rotateZ) { rotateZ ->
                                    frame.runTransfer0<String>(event) { event ->
                                        val context = frame.getContext()
                                        if (selector != null) {
                                            frame.createContainer(selector).thenAccept {
                                                val entities =
                                                    it.filterIsInstance<Target.Entity>().filter { it.isLiving }
                                                        .mapNotNull { it.asLivingEntity }
                                                submit {
                                                    future.complete(
                                                        execute(
                                                            context,
                                                            name,
                                                            entities,
                                                            type,
                                                            step,
                                                            event,
                                                            rotateX,
                                                            rotateY,
                                                            rotateZ
                                                        )
                                                    )
                                                }
                                            }
                                        } else {
                                            val player = frame.asPlayer()
                                            if (player != null) {
                                                submit {
                                                    future.complete(
                                                        execute(
                                                            context,
                                                            name,
                                                            listOf(player),
                                                            type,
                                                            step,
                                                            event,
                                                            rotateX,
                                                            rotateY,
                                                            rotateZ
                                                        )
                                                    )
                                                }
                                            } else {
                                                future.complete(null)
                                            }
                                        }
                                    }
                                }
                            }
                        }


                    }
                }

            }

            return future
        }


    }

    enum class Type(val clazz: Class<out Projectile>) {

        ARROW(Arrow::class.java), FIRE_BALL(Fireball::class.java), SNOW_BALL(Snowball::class.java)

    }


    companion object {

        /**
         * projectile type name <step: action(0.4)> <rotateX: action(0.0)>  <rotateY: action(0.0)>  <rotateZ: action(0.0)> <oncapture: block> <selector>
         */
        @KetherParser(["projectile"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            ActionLaunch(
                it.nextParsedAction(),
                it.nextParsedAction(),
                it.tryGet(arrayOf("step"), 0.4)!!,
                it.tryGet(arrayOf("rotateX"), 0.0)!!,
                it.tryGet(arrayOf("rotateY"), 0.0)!!,
                it.tryGet(arrayOf("rotateZ"), 0.0)!!,
                it.tryGet(arrayOf("oncapture"), "none")!!,
                it.selectorAction()
            )
        }

        @SubscribeEvent
        fun e(e: ProjectileHitEvent) {

            if (e.hitEntity != null) {
                val owner = e.entity.getMetadata("owner").getOrNull(0)?.value() as? LivingEntity ?: return
                val context = e.entity.getMetadata("context").getOrNull(0)?.value() as? Session ?: return
                val event = e.entity.getMetadata("event").getOrNull(0)?.asString() ?: return
                context.handleIncident(event, IncidentHitEntity(owner, e.hitEntity!!))
            }
            e.entity.remove()

        }


    }

}