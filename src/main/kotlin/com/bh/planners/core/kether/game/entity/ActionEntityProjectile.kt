package com.bh.planners.core.kether.game.entity

import com.bh.planners.api.common.SimpleTimeoutTask
import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.target
import com.bh.planners.core.effect.inline.Incident.Companion.handleIncident
import com.bh.planners.core.effect.inline.IncidentHitBlock
import com.bh.planners.core.effect.inline.IncidentHitEntity
import com.bh.planners.core.effect.rotateAroundX
import com.bh.planners.core.effect.rotateAroundY
import com.bh.planners.core.effect.rotateAroundZ
import com.bh.planners.core.kether.common.CombinationKetherParser
import com.bh.planners.core.kether.common.KetherHelper
import com.bh.planners.core.kether.common.KetherHelper.containerOrSender
import com.bh.planners.core.kether.game.ActionVelocity.generatedVelocity
import com.bh.planners.core.kether.getContext
import com.bh.planners.core.pojo.Session
import org.bukkit.entity.*
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.ProjectileHitEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.util.getMetaFirst
import taboolib.platform.util.getMetaFirstOrNull
import taboolib.platform.util.hasMeta
import taboolib.platform.util.setMeta

@CombinationKetherParser.Used
fun projectile() = KetherHelper.simpleKetherParser<Target.Container> {
    it.group(
            text(),
            text(),
            command("step", then = double()).option().defaultsTo(0.4),
            command("gravity", then = bool()).option().defaultsTo(false),
            command("bounce", then = bool()).option().defaultsTo(false),
            command("rotate", then = double().and(double(), double())).option().defaultsTo(Triple(0.0, 0.0, 0.0)),
            command("timeout", "tick", then = long()).option().defaultsTo(20),
            command("onhit", "oncapture", then = text()).option(),
            containerOrSender()
    ).apply(it) { type, name, step, isGravity, isBounce, rotate, tick, onhit, target ->
        val t = ProjectileType.valueOf(type.uppercase().replace("-", "_"))
        now {
            val (rotateX, rotateY, rotateZ) = rotate
            val container = Target.Container()
            target.forEachLivingEntity {
                val projectile = launchProjectile(t.clazz)
                projectile.customName = name
                projectile.setGravity(isGravity)
                projectile.setBounce(isBounce)
                projectile.setMeta("@planners:projectile", true)
                projectile.setMeta("@planners:projectile-owner", this)
                onhit?.let { projectile.setMeta("@planners:projectile-event", it) }
                projectile.setMeta("@planners:projectile-context", getContext())
                generatedVelocity {
                    rotateAroundX(this, rotateX)
                    rotateAroundY(this, rotateY)
                    rotateAroundZ(this, rotateZ)
                    multiply(step)
                }

                container += projectile.target()
            }

            SimpleTimeoutTask.createSimpleTask(tick, false) {
                container.forEachLivingEntity { remove() }
            }

            container
        }
    }
}

private enum class ProjectileType(val clazz: Class<out Projectile>) {

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

@SubscribeEvent(priority = EventPriority.LOW)
fun e(e: EntityDamageByEntityEvent) {
    if (e.damager is Projectile && e.damager.hasMeta("@planners:projectile")) {
        e.isCancelled = true
    }
}

@SubscribeEvent
fun e(e: ProjectileHitEvent) {
    // 忽略其他目标
    if (!e.entity.hasMeta("@planners:projectile")) {
        return
    }
    // 忽略被击目标
    if (e.hitEntity?.hasMeta("ignore-hit") == true) {
        return
    }
    val projectile = e.entity
    val event = projectile.getMetaFirstOrNull("@planners:projectile-event") ?: return

    val owner = projectile.getMetaFirst("@planners:projectile-owner").value() as LivingEntity
    val context = projectile.getMetaFirst("@planners:projectile-context").value() as? Session ?: return

    if (e.hitEntity != null) {
        context.handleIncident(event.value()!!.toString(), IncidentHitEntity(projectile,owner, e.hitEntity!!, e))
    }
    if (e.hitBlock != null) {
        context.handleIncident(event.value()!!.toString(), IncidentHitBlock(projectile,owner, e.hitBlock!!, e))
    }
}
