package com.bh.planners.core.kether.compat.attsystem

import com.bh.planners.core.kether.*
import com.skillw.attsystem.AttributeSystem
import org.bukkit.metadata.FixedMetadataValue
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.KetherParser
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.scriptParser
import taboolib.platform.BukkitPlugin
import java.util.concurrent.CompletableFuture

class ActionAttDamage {

    class Attack(val value: ParsedAction<*>, val selector: ParsedAction<*>) : ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            return frame.newFrame(value).run<Any>().thenAccept { key ->
                val asPlayer = frame.asPlayer() ?: return@thenAccept
                frame.execLivingEntity(selector) {
                    catchRunning {
                        this.setMetadata("Planners:Attack", FixedMetadataValue(BukkitPlugin.getInstance(), true))
                        val damage =
                            AttributeSystem.attributeSystemAPI.playerAttackCal(key.toString(), asPlayer, this) {}
                        AttributeSystem.attributeSystemAPI.skipNextDamageCal()
                        this.damage(damage, asPlayer)
                        this.setMetadata("Planners:Attack", FixedMetadataValue(BukkitPlugin.getInstance(), false))
                        this.noDamageTicks = 0
                    }
                }
            }
        }
    }

    companion object {

        /**
         * 对selector目标攻击,
         * attack [damage] [selector]
         * attack 10.0 "-@aline 10"
         */
        @KetherParser(["as-attack"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            Attack(it.next(ArgTypes.ACTION), it.selector())
        }

    }

}