package com.bh.planners.core.kether.compat.attsystem

import com.bh.planners.core.kether.*
import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.fight.FightData
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
                        val data = FightData(asPlayer, this).apply {
                            frame.variables().run {
                                forEach {(key,value)-> this@apply.put(key,value) }
                            }
                        }
                        val damage =
                            AttributeSystem.attributeSystemAPI.runFight(key.toString(),data , true)
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
         * 对selector目标进行AS战斗机制组攻击,
         * as-attack [战斗机制组id] [selector]
         * as-attack "Example-Skill" "-@aline 10"
         */
        @KetherParser(["as-attack"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            Attack(it.next(ArgTypes.ACTION), it.selector())
        }

    }

}