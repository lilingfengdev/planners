package com.bh.planners.core.kether.game.entity

import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.effect.inline.Incident.Companion.handleIncident
import com.bh.planners.core.effect.inline.IncidentHitEntity
import com.bh.planners.core.effect.rotateAroundX
import com.bh.planners.core.effect.rotateAroundY
import com.bh.planners.core.effect.rotateAroundZ
import com.bh.planners.core.kether.*
import com.bh.planners.core.pojo.Context
import com.bh.planners.core.pojo.Session
import org.bukkit.entity.*
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.ProjectileHitEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import taboolib.platform.util.getMeta
import taboolib.platform.util.hasMeta
import taboolib.platform.util.setMeta
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
        ScriptAction<Target.Container>() {


        override fun run(frame: ScriptFrame): CompletableFuture<Target.Container> {

            val future = CompletableFuture<Target.Container>()
            frame.run(action).str {
                val type = Type.valueOf(it.toUpperCase())
                frame.run(name).str { name ->
                    frame.run(step).double { step ->
                        frame.run(rotateX).double { rotateX ->
                            frame.run(rotateY).double { rotateY ->
                                frame.run(rotateZ).double { rotateZ ->
                                    frame.run(event).str { event ->
                                        val context = frame.getContext()
                                        val container = Target.Container()
                                        frame.containerOrSender(selector).thenAccept {
                                            val entities = it.filterIsInstance<Target.Entity>()
                                            submit {
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
                                                ).forEach {
                                                    container += it.toTarget()
                                                }
                                                future.complete(container)
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
                it.nextArgumentAction(arrayOf("step"), 0.4)!!,
                it.nextArgumentAction(arrayOf("rotateX"), 0.0)!!,
                it.nextArgumentAction(arrayOf("rotateY"), 0.0)!!,
                it.nextArgumentAction(arrayOf("rotateZ"), 0.0)!!,
                it.nextArgumentAction(arrayOf("oncapture", "onhit"), "none")!!,
                it.nextSelectorOrNull()
            )
        }

        /**
         * it.group(
        text(), text(),
        command("step", then = double()).option(),
        command("rotateX", then = double()).option(),
        command("rotateY", then = double()).option(),
        command("rotateZ", then = double()).option(),
        command("oncapture", then = double()).option()
        ).apply(it) { type,name,step,rotateX,rotateY,rotateZ,oncapture ->
        now {
        execute(getContext(),step,)
        }
        }
         *
         */

        @SubscribeEvent(priority = EventPriority.LOW)
        fun e(e: EntityDamageByEntityEvent) {
            if (e.damager is Projectile && e.damager.hasMeta("@Planners:Projectile")) {
                e.isCancelled = true
            }
        }

        @SubscribeEvent(ignoreCancelled = true)
        fun e(e: ProjectileHitEvent) {
            if (e.hitEntity != null) {
                val owner = e.entity.getMeta("owner").getOrNull(0)?.value() as? LivingEntity ?: return
                val context = e.entity.getMeta("context").getOrNull(0)?.value() as? Session ?: return
                val event = e.entity.getMeta("event").getOrNull(0)?.asString() ?: return
                context.handleIncident(event, IncidentHitEntity(owner, e.hitEntity!!, e))

            }

            // 是pl实体总是删除
            if (e.entity.hasMeta("@Planners:Projectile")) {
                e.entity.remove()
            }

        }


        fun execute(
            context: Context,
            name: String,
            owners: List<Target.Entity>,
            type: Type,
            step: Double,
            event: String,
            rotateX: Double,
            rotateY: Double,
            rotateZ: Double,
        ): List<Projectile> {
            val listOf = mutableListOf<Projectile>()
            owners.forEach {
                val entity = it.bukkitLivingEntity ?: return@forEach
                val projectile = entity.launchProjectile(type.clazz)
                if (event != "none") {
                    projectile.setMeta("owner", entity)
                    projectile.setMeta("event", event)
                    projectile.setMeta("context", context)
                }

                projectile.setMeta("@Planners:Projectile", true)
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
    }

}