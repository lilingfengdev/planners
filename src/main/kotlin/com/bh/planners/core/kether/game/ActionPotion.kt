package com.bh.planners.core.kether.game

import com.bh.planners.core.kether.*
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import taboolib.common.platform.function.submit
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Zaphkiel
 * ink.ptms.zaphkiel.module.kether.ActionEffect
 *
 * @author sky
 * @since 2021/3/16 2:56 下午
 */
class ActionPotion {

    class Give(
        val name: ParsedAction<*>,
        val duration: ParsedAction<*>,
        val amplifier: ParsedAction<*>,
        val selector: ParsedAction<*>?,
    ) : ScriptAction<Void>() {

        fun execute(entity: LivingEntity, effectType: PotionEffectType?, duration: Int, amplifier: Int) {
            entity.addPotionEffect(PotionEffect(effectType ?: return, duration, amplifier))
        }

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            frame.readAccept<String>(name) { name ->
                frame.readAccept<Int>(duration) { duration ->
                    frame.readAccept<Int>(amplifier) { amplifier ->
                        val effectType = PotionEffectType.getByName(name.uppercase(Locale.getDefault()))

                        if (selector != null) {
                            frame.createContainer(selector).thenAccept {
                                submit {
                                    it.forEachLivingEntity { execute(this, effectType, duration, amplifier) }
                                }
                            }
                        } else {
                            val viewer = frame.script().sender?.castSafely<Player>() ?: error("No player selected.")
                            submit {
                                execute(viewer, effectType, duration, amplifier)
                            }
                        }

                    }

                }
            }
            return CompletableFuture.completedFuture(null)
        }
    }

    class Remove(val name: ParsedAction<*>, val selector: ParsedAction<*>?) : ScriptAction<Void>() {

        fun execute(entity: LivingEntity, effectType: PotionEffectType?) {
            if (effectType != null) {
                entity.removePotionEffect(effectType)
            }
        }

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            frame.newFrame(name).run<Any>().thenApplyAsync({ name ->
                val effectType = PotionEffectType.getByName(name.toString().uppercase(Locale.getDefault()))

                if (selector != null) {
                    frame.execLivingEntity(selector) { execute(this, effectType) }
                } else {
                    val viewer = frame.script().sender?.castSafely<Player>() ?: error("No player selected.")
                    execute(viewer, effectType)
                }

            }, frame.context().executor)
            return CompletableFuture.completedFuture(null)
        }
    }

    class Clear(val selector: ParsedAction<*>?) : ScriptAction<Void>() {

        fun execute(entity: LivingEntity) {
            entity.activePotionEffects.toList().forEach { entity.removePotionEffect(it.type) }
        }

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            submit {
                if (selector != null) {
                    frame.execLivingEntity(selector) { execute(this) }
                } else {
                    val viewer = frame.script().sender?.castSafely<Player>() ?: error("No player selected.")
                    execute(viewer)
                }
            }
            return CompletableFuture.completedFuture(null)
        }
    }

    companion object {

        /**
         * potion give SPEED 10 10 <they selector>
         * potion clear <they selector>
         * potion remove SPEED <they selector>
         */
        @KetherParser(["potion"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            it.switch {
                case("give") {
                    Give(
                        it.nextParsedAction(),
                        it.nextParsedAction(),
                        it.nextParsedAction(),
                        it.nextSelectorOrNull()
                    )
                }
                case("clear") { Clear(it.nextSelectorOrNull()) }
                case("remove") { Remove(it.nextParsedAction(), it.nextSelectorOrNull()) }
            }
        }
    }
}