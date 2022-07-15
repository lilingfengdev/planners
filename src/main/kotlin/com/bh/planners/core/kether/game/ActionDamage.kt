package com.bh.planners.core.kether.game

import com.bh.planners.core.kether.*
import com.bh.planners.util.eval
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.metadata.FixedMetadataValue
import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import taboolib.platform.BukkitPlugin
import java.util.concurrent.CompletableFuture

class ActionDamage {

    class Damage(val value: ParsedAction<*>, val selector: ParsedAction<*>) : ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            return frame.newFrame(value).run<Any>().thenAccept { damage ->
                frame.execLivingEntity(selector) {
                    this.damage(damage.toString().eval(this.maxHealth))
                    this.noDamageTicks = 0
                }
            }
        }
    }

    class Attack(val value: ParsedAction<*>, val selector: ParsedAction<*>) : ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            return frame.newFrame(value).run<Any>().thenAccept { damage ->
                val asPlayer = frame.asPlayer() ?: return@thenAccept
                frame.execLivingEntity(selector) {
                    catchRunning {
                        this.setMetadata("Planners:Attack", FixedMetadataValue(BukkitPlugin.getInstance(), true))
                        this.damage(damage.toString().eval(this.maxHealth), asPlayer)
                        this.setMetadata("Planners:Attack", FixedMetadataValue(BukkitPlugin.getInstance(), false))
                        this.noDamageTicks = 0
                    }
                }
            }
        }
    }

    fun obtain(livingEntity: LivingEntity, experience: String) {

    }

    companion object {

        /**
         * 对selector目标造成伤害
         * damage [damage] [selector]
         * damage 10.0 "-@aline 10"
         */
        @KetherParser(["damage"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            Damage(it.next(ArgTypes.ACTION), it.selector())
        }

        /**
         * 对selector目标攻击,
         * attack [damage] [selector]
         * attack 10.0 "-@aline 10"
         */
        @KetherParser(["attack"], namespace = NAMESPACE, shared = true)
        fun parser2() = scriptParser {
            Attack(it.next(ArgTypes.ACTION), it.selector())
        }

    }

}