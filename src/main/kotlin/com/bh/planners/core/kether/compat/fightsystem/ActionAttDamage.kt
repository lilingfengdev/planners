//package com.bh.planners.core.kether.compat.fightsystem
//
//import com.bh.planners.core.kether.*
//import com.skillw.fightsystem.api.FightAPI
//import com.skillw.fightsystem.api.fight.FightData
//import org.bukkit.Bukkit
//import org.bukkit.metadata.FixedMetadataValue
//import taboolib.common.LifeCycle
//import taboolib.common.platform.Awake
//import taboolib.library.kether.ParsedAction
//import taboolib.module.kether.KetherParser
//import taboolib.module.kether.ScriptAction
//import taboolib.module.kether.ScriptFrame
//import taboolib.module.kether.scriptParser
//import taboolib.platform.BukkitPlugin
//import taboolib.platform.util.getMetaFirstOrNull
//import java.util.concurrent.CompletableFuture
//
//
//class ActionAttDamage {
//
//    class Attack(val value: ParsedAction<*>, val selector: ParsedAction<*>) : ScriptAction<Void>() {
//        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
//            return frame.newFrame(value).run<Any>().thenAccept { key ->
//                val player = frame.bukkitPlayer() ?: return@thenAccept
//                frame.container(selector).thenAccept {
//                    it.forEachLivingEntity {
//                        catchRunning {
//                            this.setMetadata("Planners:Attack", FixedMetadataValue(BukkitPlugin.getInstance(), true))
//                            val data = FightData(player, this).apply {
//                                frame.variables().run {
//                                    forEach { (key, value) -> this@apply.put(key, value) }
//                                }
//                            }
//                            FightAPI.runFight(key.toString(), data, true)
//                            this.setMetadata("Planners:Attack", FixedMetadataValue(BukkitPlugin.getInstance(), false))
//                            this.noDamageTicks = 0
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    companion object {
//        @Awake(LifeCycle.ENABLE)
//        fun ignore() {
//            if (Bukkit.getPluginManager().isPluginEnabled("FightSystem")) {
//                FightAPI.addIgnoreAttack { _, defender ->
//                    defender.getMetaFirstOrNull("Planners:Attack")?.asBoolean() == true
//                }
//            }
//        }
//
//        /**
//         * 对selector目标进行AS战斗机制组攻击, as-attack [战斗机制组id] [selector] as-attack
//         * "Example-Skill" "@aline 10"
//         */
//        @KetherParser(["as-attack"], namespace = NAMESPACE, shared = true)
//        fun parser1() = scriptParser {
//            Attack(it.nextParsedAction(), it.nextSelector())
//        }
//
//        @KetherParser(["fs-attack"], namespace = NAMESPACE, shared = true)
//        fun parser2() = scriptParser {
//            Attack(it.nextParsedAction(), it.nextSelector())
//        }
//
//    }
//
//}