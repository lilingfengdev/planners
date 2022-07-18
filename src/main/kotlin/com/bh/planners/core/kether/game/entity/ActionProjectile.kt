package com.bh.planners.core.kether.game.entity

import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.inline.CaptureEntity
import com.bh.planners.core.effect.inline.InlineEvent.Companion.callEvent
import com.bh.planners.core.kether.*
import com.bh.planners.core.pojo.Context
import com.bh.planners.core.pojo.Session
import org.bukkit.entity.Arrow
import org.bukkit.entity.Entity
import org.bukkit.entity.Fireball
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Projectile
import org.bukkit.entity.Snowball
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.metadata.FixedMetadataValue
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.KetherParser
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.scriptParser
import taboolib.platform.BukkitPlugin
import java.util.concurrent.CompletableFuture

class ActionProjectile {

    class ActionLaunch(
        val action: ParsedAction<*>,
        val name: ParsedAction<*>,
        val step: ParsedAction<*>,
        val event: ParsedAction<*>,
        val selector: ParsedAction<*>?
    ) :
        ScriptAction<List<Entity>>() {

        fun execute(
            context: Context,
            name: String,
            owners: List<LivingEntity>,
            type: Type,
            step: Double,
            event: String
        ): List<Projectile> {
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
                projectile.velocity = projectile.velocity.multiply(step);
                listOf += projectile
            }
            return listOf
        }

        override fun run(frame: ScriptFrame): CompletableFuture<List<Entity>> {

            val future = CompletableFuture<List<Entity>>()
            frame.runTransfer<Type>(action) { type ->
                frame.runTransfer<String>(name) { name ->
                    frame.runTransfer<Double>(step) { step ->
                        frame.runTransfer<String>(event) { event ->
                            val context = frame.getContext()
                            if (selector != null) {
                                frame.createContainer(selector).thenAccept {
                                    val entities = it.targets.filterIsInstance<Target.Entity>().filter { it.isLiving }
                                        .map { it.entity } as List<LivingEntity>
                                    submit { future.complete(execute(context, name, entities, type, step, event)) }
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
                                                event
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

            return future
        }

    }

    enum class Type(val clazz: Class<out Projectile>) {

        ARROW(Arrow::class.java), FIRE_BALL(Fireball::class.java), SNOW_BALL(Snowball::class.java)

    }


    companion object {

        /**
         * projectile type name <step: action(0.4)> <event,e: action> <selector>
         */
        @KetherParser(["projectile"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            ActionLaunch(
                it.next(ArgTypes.ACTION),
                it.next(ArgTypes.ACTION),
                it.tryGet(arrayOf("step"), 0.4)!!,
                it.tryGet(arrayOf("event", "e"), "none")!!,
                it.selectorAction()
            )
        }

        @SubscribeEvent
        fun e(e: ProjectileHitEvent) {

            if (e.hitEntity != null) {
                val owner = e.entity.getMetadata("owner").getOrNull(0)?.value() as? LivingEntity ?: return
                val context = e.entity.getMetadata("context").getOrNull(0)?.value() as? Session ?: return
                val event = e.entity.getMetadata("event").getOrNull(0)?.asString() ?: return
                context.callEvent(event, owner, CaptureEntity(e.hitEntity!!))
            }


        }


    }

}