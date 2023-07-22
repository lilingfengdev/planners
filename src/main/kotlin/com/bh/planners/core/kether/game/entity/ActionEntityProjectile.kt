package com.bh.planners.core.kether.game.entity

import com.bh.planners.api.common.SimpleTimeoutTask
import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.effect.inline.Incident.Companion.handleIncident
import com.bh.planners.core.effect.inline.IncidentHitBlock
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
import taboolib.library.kether.QuestContext
import taboolib.module.kether.*
import taboolib.platform.util.getMeta
import taboolib.platform.util.hasMeta
import taboolib.platform.util.setMeta
import java.util.*
import java.util.concurrent.CompletableFuture

class ActionEntityProjectile {

    class ActionProjectile(
        val action: ParsedAction<*>,
        val name: ParsedAction<*>,
        val step: ParsedAction<*>,
        val gravity: ParsedAction<*>,
        val bounce: ParsedAction<*>,
        val rotateX: ParsedAction<*>,
        val rotateY: ParsedAction<*>,
        val rotateZ: ParsedAction<*>,
        val tick: ParsedAction<*>,
        val event: ParsedAction<*>,
        val selector: ParsedAction<*>?,
    ) : ScriptAction<Target.Container>() {


        override fun run(frame: ScriptFrame): CompletableFuture<Target.Container> {

            val future = CompletableFuture<Target.Container>()
            frame.run(action).str {
                val type = Type.valueOf(it.uppercase(Locale.getDefault()))
                frame.run(name).str { name ->
                    frame.run(step).double { step ->
                        frame.run(gravity).bool { gravity ->
                            frame.run(bounce).bool { bounce ->
                                frame.run(rotateX).double { rotateX ->
                                    frame.run(rotateY).double { rotateY ->
                                        frame.run(rotateZ).double { rotateZ ->
                                            frame.run(tick).long { tick ->
                                                frame.run(event).str { event ->
                                                    val context = frame.getContext()
                                                    val container = Target.Container()
                                                    frame.containerOrSender(selector).thenAccept {
                                                        val entities = it.filterIsInstance<Target.Entity>()
                                                        submit {
                                                            execute(
                                                                frame.variables(),
                                                                context,
                                                                name,
                                                                entities,
                                                                type,
                                                                step,
                                                                gravity,
                                                                bounce,
                                                                event,
                                                                rotateX,
                                                                rotateY,
                                                                rotateZ,
                                                                tick
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

                    }
                }

            }

            return future
        }


    }

    enum class Type(val clazz: Class<out Projectile>) {

        ARROW(Arrow::class.java),
        DRAGON_FIRE_BALL(DragonFireball::class.java),
        EGG(Egg::class.java),
        ENDER_PEARL(EnderPearl::class.java),
        FIRE_BALL(Fireball::class.java),
        FISH_HOOK(FishHook::class.java),
        LARGE_FIRE_BALL(LargeFireball::class.java),
        LINGERING_POTION(LingeringPotion::class.java),
        LLAMA_SPIT(LlamaSpit::class.java),
        SHULKER_BULLET(ShulkerBullet::class.java),
        SMALL_FIRE_BALL(SmallFireball::class.java),
        SNOW_BALL(Snowball::class.java),
        SPECTRAL_ARROW(SpectralArrow::class.java),
        SPLASH_POTION(SplashPotion::class.java),
        THROWN_EXP_BOTTLE(ThrownExpBottle::class.java),
        THROWN_POTION(ThrownPotion::class.java),
        TIPPED_ARROW(TippedArrow::class.java),
        WITHER_SKULL(WitherSkull::class.java)

    }


    companion object {

        /**
         * projectile type name <step: action(0.4)> <gravity: Boolean> <bounce: Boolean> <rotateX: action(0.0)>  <rotateY: action(0.0)>  <rotateZ: action(0.0)> <timeout: 20> <oncapture: block> <selector>
         */
        @KetherParser(["projectile"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            ActionProjectile(
                it.nextParsedAction(),
                it.nextParsedAction(),
                it.nextArgumentAction(arrayOf("step"), 0.4)!!,
                it.nextArgumentAction(arrayOf("gravity"), "false")!!,
                it.nextArgumentAction(arrayOf("bounce"), "false")!!,
                it.nextArgumentAction(arrayOf("rotateX"), 0.0)!!,
                it.nextArgumentAction(arrayOf("rotateY"), 0.0)!!,
                it.nextArgumentAction(arrayOf("rotateZ"), 0.0)!!,
                it.nextArgumentAction(arrayOf("timeout"), 20)!!,
                it.nextArgumentAction(arrayOf("oncapture", "onhit"), "none")!!,
                it.nextSelectorOrNull()
            )
        }

        @SubscribeEvent(priority = EventPriority.LOW)
        fun e(e: EntityDamageByEntityEvent) {
            if (e.damager is Projectile && e.damager.hasMeta("@Planners:Projectile")) {
                e.isCancelled = true
            }
        }

        @SubscribeEvent
        fun e(e: ProjectileHitEvent) {
            if (e.hitEntity?.hasMeta("ignoreHit") == true) return
            val owner = e.entity.getMeta("owner").getOrNull(0)?.value() as? LivingEntity ?: return
            val context = e.entity.getMeta("context").getOrNull(0)?.value() as? Session ?: return
            val event = e.entity.getMeta("event").getOrNull(0)?.asString() ?: return
            val vars = e.entity.getMeta("vars").getOrNull(0)?.value() as? QuestContext.VarTable ?: return
            if (e.hitEntity != null) {
                context.handleIncident(event, IncidentHitEntity(owner, e.hitEntity!!, e, e.entity, vars))
            }
            if (e.hitBlock != null) {
                context.handleIncident(event, IncidentHitBlock(owner, e.hitBlock!!, e, e.entity, vars))
            }
        }


        fun execute(
            vars: QuestContext.VarTable,
            context: Context,
            name: String,
            owners: List<Target.Entity>,
            type: Type,
            step: Double,
            gravity: Boolean,
            bounce: Boolean,
            event: String,
            rotateX: Double,
            rotateY: Double,
            rotateZ: Double,
            tick: Long,
        ): List<Projectile> {
            val listOf = mutableListOf<Projectile>()
            owners.forEach {
                val entity = it.bukkitLivingEntity ?: return@forEach
                val projectile = entity.launchProjectile(type.clazz)
                // 注册销毁任务
                SimpleTimeoutTask.createSimpleTask(tick, false) {
                    if (projectile.isValid) {
                        projectile.remove()
                    }
                }

                if (event != "none") {
                    projectile.setMeta("owner", entity)
                    projectile.setMeta("event", event)
                    projectile.setMeta("context", context)
                    projectile.setMeta("vars", vars)
                }

                projectile.setMeta("@Planners:Projectile", true)
                projectile.setGravity(gravity)
                projectile.setBounce(bounce)
                projectile.customName = name
                projectile.isCustomNameVisible = false
                val velocity = projectile.velocity

                // 处理向量旋转
                rotateAroundX(velocity, rotateX)
                rotateAroundY(velocity, rotateY)
                rotateAroundZ(velocity, rotateZ)

                projectile.velocity = velocity.multiply(step)
                listOf += projectile
            }
            return listOf
        }
    }

}