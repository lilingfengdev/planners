package com.bh.planners.core.kether.compat.germplugin

import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.getEntity
import com.bh.planners.core.effect.Target.Companion.getLocation
import com.bh.planners.core.kether.ACTION_NULL
import com.bh.planners.core.kether.containerOrSender
import com.germ.germplugin.api.GermSrcManager
import com.germ.germplugin.api.RootType
import com.germ.germplugin.api.dynamic.animation.GermAnimationMove
import com.germ.germplugin.api.dynamic.animation.IAnimatable
import com.germ.germplugin.api.dynamic.effect.GermEffectPart
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.LivingEntity
import taboolib.common.platform.function.info
import taboolib.common5.cdouble
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer


class ActionGermEffectProjectile : ScriptAction<Void>() {

    lateinit var id: ParsedAction<*>
    lateinit var duration: ParsedAction<*>
    lateinit var delay: ParsedAction<*>
    lateinit var transition: ParsedAction<*>
    lateinit var yaw: ParsedAction<*>
    lateinit var pitch: ParsedAction<*>
    lateinit var onhit: ParsedAction<*>
    lateinit var collisionCount: ParsedAction<*>
    lateinit var collisionRemove: ParsedAction<*>
    var selector: ParsedAction<*>? = null
    lateinit var to: ParsedAction<*>

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        frame.run(id).str { id ->
            frame.run(duration).str { duration ->
                frame.run(delay).long { delay ->
                    frame.run(transition).str { transition ->
                        frame.run(yaw).str { yaw ->
                            frame.run(pitch).str { pitch ->
                                frame.run(onhit).str { onhit ->
                                    frame.run(collisionCount).int { collisionCount ->
                                        frame.run(collisionRemove).bool { collisionRemove ->
                                            frame.containerOrSender(selector).thenAccept { source ->
                                                frame.containerOrSender(to).thenAccept { to ->
                                                    try {
                                                        execute(id, source, to, duration, delay, transition, yaw, pitch, onhit, collisionCount, collisionRemove)
                                                    } catch (t: Throwable) {
                                                        t.printStackTrace()
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
        return CompletableFuture.completedFuture(null)
    }

    fun execute(id: String, source: Target.Container, to: Target.Container, duration: String, delay: Long, transition: String, yaw: String, pitch: String, onhit: String, count: Int, remove: Boolean) {
        val effect = GermEffectPart.getGermEffectPart(newIndexName(), getEffectSrc(id)) ?: error("No effect $id found.")
        effect.duration = duration
        to.forEach { target ->
            source.forEach source@{ source ->
                val origin = source.getLocation() ?: return@source
                val effect = mixtureCreateEffect(effect)
                val animatable = effect as IAnimatable<*>

                // 绑定目标目的地

                var move : GermAnimationMove? = null

                if (target is Target.Location) {
                    move = buildMoveToTarget(origin, target.getLocation()!!, transition).setDelay(delay)
                    animatable.addAnimation(move)
                }

                origin.world!!.players.forEach { onlinePlayer ->
                    // 命中实体
                    effect.isCollisionEntity = true
                    effect.isCollisionBlock = true
                    effect.collisionCount = count
                    effect.isCollisionRemove = remove

                    // 适配Java
//                    effect.setOnEntity(Consumer {
//                        info("on entity $it")
//                    })
                    effect.setOnEntity {
                        info("on entity $it")
                    }
                    effect.setOnBlock {
                        info("on block $it")
                    }

                    // 播放到实体
                    if (target is Target.Entity) {
                        effect.shooterName = source.getEntity()!!.name
                        setOnCollision(effect.shooterName,effect,10.0)


                        if (yaw == ACTION_NULL && pitch == ACTION_NULL) {
                            effect.spawnToEntity(onlinePlayer, source.getEntity())
                        }
                        // 绑定yaw和pitch
                        else {
                            effect.spawnToEntity(onlinePlayer, source.getEntity(), pitch.cdouble, yaw.cdouble, 0.0)
                        }

                    }

                    if (target is Target.Location) {
                        if (yaw == ACTION_NULL && pitch == ACTION_NULL) {
                            effect.spawnToLocation(onlinePlayer, origin)
                        }
                        // 绑定yaw和pitch
                        else {
                            effect.spawnToLocation(onlinePlayer, origin.x, origin.y, origin.z, pitch.cdouble, yaw.cdouble, 0.0)
                        }
                    }

                }

            }
        }
    }


    private fun setOnCollision(shooterName: String, effectPart: GermEffectPart<*>, damage: Double) {
        if (damage <= 0) return
        effectPart.shooterName = shooterName
        effectPart.isCollisionEntity = true
        effectPart.isCollisionBlock = true
        effectPart.setCollisionCount(10)
        effectPart.setCollisionRemove(false)
        effectPart.setOnEntity(Consumer { entity ->
            info(entity)
        })
    }

    fun execute(effect: GermEffectPart<*>, move: String, source: Target.Container, target: Target.Entity) {

        source.forEachLocation {
            val effect = mixtureCreateEffect(effect)
            val animatable = effect as IAnimatable<*>
            animatable.addAnimation(buildMoveToTarget(this, target.getLocation()!!, move))
        }
    }


    fun buildMoveToTarget(casterLoc: Location, targetLoc: Location, duration: String): GermAnimationMove {
        val moveX: Double = targetLoc.getX() - casterLoc.getX()
        val moveY: Double = targetLoc.getY() - casterLoc.getY()
        val moveZ: Double = targetLoc.getZ() - casterLoc.getZ()
        val id2: UUID = UUID.randomUUID()
        val animationMove = GermAnimationMove(id2.toString())
        animationMove.delay = 0
        animationMove.cycle = 1
        animationMove.isPermanent = true
        animationMove.duration = duration.toLong()
        animationMove.setMoveX(moveX)
        animationMove.setMoveY(moveY)
        animationMove.setMoveZ(moveZ)
        animationMove.setOffsetX(0)
        animationMove.setOffsetY(0)
        animationMove.setOffsetZ(0)
        return animationMove
    }

    fun getEffectSrc(effectName: String?): ConfigurationSection? {
        val germSrcManager = GermSrcManager.getGermSrcManager()
        for (srcSet in germSrcManager.getSrcSets(RootType.EFFECT)) {
            if (!srcSet.src.contains(effectName!!)) {
                continue
            }
            val configurationSection = srcSet.src.getConfigurationSection(effectName)!!
            if (!configurationSection.contains("type")) {
                continue
            }
            return configurationSection
        }
        return null
    }

    private fun mixtureCreateEffect(effect: GermEffectPart<*>): GermEffectPart<*> {
        val clone = effect.clone() as GermEffectPart<*>
        clone.indexName = newIndexName()
        return clone
    }

    private fun newIndexName(): String? {
        return UUID.randomUUID().toString()
    }

}