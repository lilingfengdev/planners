package com.bh.planners.core.kether.game

import com.bh.planners.core.effect.Target
import com.bh.planners.core.kether.*
import com.bh.planners.core.pojo.Context
import org.bukkit.Bukkit
import org.bukkit.entity.Arrow
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Projectile
import org.bukkit.metadata.FixedMetadataValue
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

    class ActionLaunch(val action: ParsedAction<*>, val step: ParsedAction<*>, val selector: ParsedAction<*>?) :
        ScriptAction<List<Entity>>() {

        fun execute(context: Context, owners: List<LivingEntity>, type: Type, step: Double): List<Projectile> {
            val listOf = mutableListOf<Projectile>()
            owners.forEach {
                val projectile = it.launchProjectile(type.clazz)
                projectile.setMetadata("owner", FixedMetadataValue(BukkitPlugin.getInstance(), it))
                projectile.setMetadata("context", FixedMetadataValue(BukkitPlugin.getInstance(), context))
                projectile.customName = "Planners ${type.name}";
                projectile.isCustomNameVisible = false
                projectile.velocity = projectile.velocity.multiply(step);
                listOf += projectile
            }
            return listOf
        }

        override fun run(frame: ScriptFrame): CompletableFuture<List<Entity>> {

            val future = CompletableFuture<List<Entity>>()
            frame.runTransfer<Type>(action) { type ->
                frame.runTransfer<Double>(step) { step ->
                    val context = frame.getContext()
                    if (selector != null) {
                        frame.createContainer(selector).thenAccept {
                            val entities = it.targets.filterIsInstance<Target.Entity>().filter { it.isLiving }
                                .map { it.entity } as List<LivingEntity>
                            submit { future.complete(execute(context, entities, type, step)) }
                        }
                    } else {
                        val player = frame.asPlayer()
                        if (player != null) {
                            submit { future.complete(execute(context, listOf(player), type, step)) }
                        } else {
                            future.complete(null)
                        }
                    }

                }
            }

            return future
        }

    }

    enum class Type(val clazz: Class<out Projectile>) {

        ARROW(Arrow::class.java)

    }


    companion object {

        @KetherParser(["projectile"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            ActionLaunch(it.next(ArgTypes.ACTION), it.tryGet(arrayOf("step"), 0.4)!!, it.selectorAction())
        }

    }

}