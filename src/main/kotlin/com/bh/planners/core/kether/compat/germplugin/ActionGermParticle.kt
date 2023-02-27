package com.bh.planners.core.kether.compat.germplugin

import ac.github.oa.taboolib.common.reflect.Reflex.Companion.invokeMethod
import com.bh.planners.core.effect.Target
import com.bh.planners.core.kether.createContainer
import com.bh.planners.core.kether.origin
import com.germ.germplugin.api.GermSrcManager
import com.germ.germplugin.api.RootType
import com.germ.germplugin.api.dynamic.animation.AnimationGroup
import com.germ.germplugin.api.dynamic.animation.IAnimatable
import com.germ.germplugin.api.dynamic.effect.GermEffectPart
import com.germ.germplugin.api.dynamic.effect.GermEffectParticle
import com.germ.germplugin.api.event.GermSrcReloadEvent
import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection
import taboolib.common.platform.event.OptionalEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.info
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.*
import java.util.concurrent.CompletableFuture

class ActionGermParticle(val name: ParsedAction<*>, val animation: ParsedAction<*>, val selector: ParsedAction<*>?) :
    ScriptAction<GermEffectPart<*>>() {

    companion object {

        val cache = Collections.synchronizedMap(mutableMapOf<String, ConfigurationSection>())

        fun get(name: String, rootType: RootType = RootType.EFFECT): ConfigurationSection? {
            return cache.computeIfAbsent(name) {
                val split = name.split(":")
                GermSrcManager.getGermSrcManager().getSrc(split[0], rootType)?.getConfigurationSection(split[1])
            }
        }

        private fun create(name: String): GermEffectPart<*> {
            return GermEffectParticle.getGermEffectPart(
                UUID.randomUUID().toString(),
                get(name) ?: error("GermPlugin effect '$name' not found.")
            )
        }

        @SubscribeEvent(bind = "com.germ.germplugin.api.event.GermSrcReloadEvent")
        private fun e(e: OptionalEvent) {
            cache.clear()
        }

    }

    fun execute(target: Target, animations: List<IEffectAnimation>, effect: GermEffectPart<*>) {

        kotlin.runCatching {
            if (effect is IAnimatable<*>) {
                animations.forEach { effect.addAnimation(it.create()) }
            }
        }

        Bukkit.getOnlinePlayers().forEach {
            if (target is Target.Entity) {
                if (target.isBukkit) {
                    effect.spawnToEntity(it, target.bukkitEntity)
                }
                // 虚拟实体特殊处理
                else {
                    effect.spawnToLocation(it, target.value)
                }
            } else if (target is Target.Location) {
                effect.spawnToLocation(it, target.value)
            }
        }
    }

    override fun run(frame: ScriptFrame): CompletableFuture<GermEffectPart<*>> {
        val future = CompletableFuture<GermEffectPart<*>>()
        frame.run(name).str { name ->
            val effectParticle = create(name)

            frame.run(animation).thenAccept {
                val animations = when {

                    it is IEffectAnimation -> listOf(it)

                    it is List<*> -> it.map {
                        it as? IEffectAnimation ?: error("$it element not match 'EffectAnimation'")
                    }

                    it is String && it == "__none__" -> emptyList()

                    else -> emptyList()

                }

                if (selector != null) {
                    frame.createContainer(selector).thenAccept {
                        it.forEach { execute(it, animations, effectParticle) }
                        future.complete(effectParticle)
                    }
                } else {
                    execute(frame.origin(), animations, effectParticle)
                    future.complete(effectParticle)
                }

            }

        }
        return future
    }


}