package com.bh.planners.core.kether.enhance

import com.bh.planners.core.kether.*
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import taboolib.common.platform.function.submit
import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
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
        val selector: ParsedAction<*>?
    ) : ScriptAction<Void>() {

        fun execute(player: Player, effectType: PotionEffectType?, duration: Int, amplifier: Int) {
            player.addPotionEffect(PotionEffect(effectType ?: return, duration, amplifier))
        }

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            frame.newFrame(name).run<Any>().thenApply { name ->
                frame.newFrame(duration).run<Any>().thenApply {
                    val duration = Coerce.toInteger(it)
                    frame.newFrame(amplifier).run<Any>().thenApplyAsync({
                        val amplifier = Coerce.toInteger(it)
                        val effectType = PotionEffectType.getByName(name.toString().uppercase(Locale.getDefault()))

                        if (selector != null) {
                            frame.execPlayer(selector) { execute(this, effectType, duration, amplifier) }
                        } else {
                            val viewer = frame.script().sender?.castSafely<Player>() ?: error("No player selected.")
                            execute(viewer, effectType, duration, amplifier)
                        }

                    }, frame.context().executor)
                }
            }
            return CompletableFuture.completedFuture(null)
        }
    }

    class Remove(val name: ParsedAction<*>, val selector: ParsedAction<*>?) : ScriptAction<Void>() {

        fun execute(player: Player, effectType: PotionEffectType?) {
            if (effectType != null) {
                player.removePotionEffect(effectType)
            }
        }

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            frame.newFrame(name).run<Any>().thenApplyAsync({ name ->
                val effectType = PotionEffectType.getByName(name.toString().uppercase(Locale.getDefault()))

                if (selector != null) {
                    frame.execPlayer(selector) { execute(this, effectType) }
                } else {
                    val viewer = frame.script().sender?.castSafely<Player>() ?: error("No player selected.")
                    execute(viewer, effectType)
                }

            }, frame.context().executor)
            return CompletableFuture.completedFuture(null)
        }
    }

    class Clear(val selector: ParsedAction<*>?) : ScriptAction<Void>() {

        fun execute(player: Player) {
            player.activePotionEffects.toList().forEach { player.removePotionEffect(it.type) }
        }

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            submit {
                if (selector != null) {
                    frame.execPlayer(selector) { execute(this) }
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
         * effect give *SPEED *10 *10
         */
        @KetherParser(["potion"], namespace = NAMESPACE)
        fun parser() = scriptParser {
            it.switch {
                case("give") {
                    Give(
                        it.next(ArgTypes.ACTION),
                        it.next(ArgTypes.ACTION),
                        it.next(ArgTypes.ACTION),
                        it.selectorAction()
                    )
                }
                case("clear") { Clear(it.selectorAction()) }
                case("remove") { Remove(it.next(ArgTypes.ACTION), it.selectorAction()) }
            }
        }
    }
}