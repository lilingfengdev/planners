package com.bh.planners.core.kether.compat.germplugin

import com.bh.planners.core.effect.Target
import com.bh.planners.core.kether.createContainer
import com.bh.planners.core.kether.runTransfer0
import com.bh.planners.core.kether.target
import com.bh.planners.core.kether.toOriginLocation
import com.germ.germplugin.api.GermSrcManager
import com.germ.germplugin.api.RootType
import com.germ.germplugin.api.dynamic.effect.GermEffectPart
import com.germ.germplugin.api.dynamic.effect.GermEffectParticle
import com.germ.germplugin.api.event.GermSrcReloadEvent
import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection
import taboolib.common.platform.event.SubscribeEvent
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import java.util.*
import java.util.concurrent.CompletableFuture

class ActionGermParticle(val name: ParsedAction<*>, val selector: ParsedAction<*>?) : ScriptAction<Void>() {

    companion object {

        private val cache = Collections.synchronizedMap(mutableMapOf<String, ConfigurationSection>())


        private fun get(name: String): ConfigurationSection? {
            return cache.computeIfAbsent(name) {
                val split = name.split(":")
                GermSrcManager.getGermSrcManager().getSrc(split[0], RootType.EFFECT)?.getConfigurationSection(split[1])
            }
        }

        private fun create(name: String): GermEffectPart<*> {
            return GermEffectParticle.getGermEffectPart(
                UUID.randomUUID().toString(),
                get(name) ?: error("GermPlugin effect '$name' not found.")
            )
        }

        @SubscribeEvent
        private fun e(e: GermSrcReloadEvent) {
            cache.clear()
        }

    }

    fun execute(target: Target, effect: GermEffectPart<*>) {
        Bukkit.getOnlinePlayers().forEach {
            if (target is Target.Entity) {
                effect.spawnToEntity(it, target.entity)
            } else if (target is Target.Location) {
                effect.spawnToLocation(it, target.value)
            }
        }
    }

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        frame.runTransfer0<String>(name) { name ->
            val effectParticle = create(name)
            if (selector != null) {
                frame.createContainer(selector).thenAccept {
                    it.targets.forEach { execute(it, effectParticle) }
                }
            } else {
                execute(frame.target(), effectParticle)
            }
        }
        return CompletableFuture.completedFuture(null)
    }


}