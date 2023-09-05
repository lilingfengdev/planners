//package com.bh.planners.core.kether.compat.monsteritem
//
//import com.bh.planners.api.common.Plugin
//import com.bh.planners.core.effect.Target
//import com.bh.planners.core.kether.common.CombinationKetherParser
//import com.bh.planners.core.kether.common.KetherHelper
//import com.bh.planners.core.kether.common.KetherHelper.containerOrEmpty
//import com.bh.planners.core.kether.common.MultipleKetherParser
//import me.skymc.customized.monsteritem.api.MonsterItemAPI
//import me.skymc.customized.monsteritem.core.attribute.type.WeaponType
//import org.bukkit.entity.Player
//import taboolib.module.kether.script
//
//@CombinationKetherParser.Used
//@Plugin("MonsterItem")
//object ActionMonster : MultipleKetherParser("monster") {
//
//    val attack = KetherHelper.simpleKetherParser<Unit> {
//        it.group(command("type", then = text()).option(), double(), containerOrEmpty()).apply(it) { type, damage, container ->
//            now {
//                val weaponType = if (type != null) WeaponType.valueOf(type.uppercase()) else null
//                val source = script().sender!!.castSafely<Player>() ?: error("sender must be a player")
//                container.forEachLivingEntity {
//                    if (weaponType == null) {
//                        MonsterItemAPI.makeSkillDamage(source, this, damage, false)
//                    } else {
//                        MonsterItemAPI.makeSkillDamage(source, this, damage, weaponType, false)
//                    }
//                }
//
//            }
//        }
//    }
//
//}