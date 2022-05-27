package com.bh.planners.core.ui

import com.bh.planners.core.kether.LazyGetter
import com.bh.planners.core.kether.namespaces
import com.bh.planners.core.pojo.Skill
import com.bh.planners.core.pojo.player.PlayerJob
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.info
import taboolib.library.xseries.parseToMaterial
import taboolib.module.chat.colored
import taboolib.module.kether.KetherFunction
import taboolib.module.kether.KetherShell
import taboolib.module.kether.printKetherErrorMessage
import taboolib.platform.util.buildItem

class SkillIcon(val player: Player, skillKey: String, val level: Int) {

    companion object {
        val ZERO = LazyGetter { "?" }
    }

    private val skill = PlayerJob.Skill(-1, skillKey, level)
    private val option = skill.instance.option
    private val variables = option.variables.associate {
        it.key to if (level == 0) ZERO else toLazyVariable(skill, it, player)
    }

    fun build(): ItemStack {
        return buildItem(option.root.getString("icon.material")!!.parseToMaterial()) {
            name = format(option.root.getString("icon.name")!!)
            lore += option.root.getStringList("icon.lore").map { format(it) }
            damage = option.root.getInt("damage")
        }
    }

    private fun toLazyVariable(skill: PlayerJob.Skill, variable: Skill.Variable, player: Player): LazyGetter<*> {
        return LazyGetter {
            KetherShell.eval(variable.expression, namespace = namespaces, sender = adaptPlayer(player)) {
                rootFrame().variables()["@Skill"] = skill
                variables.filter { variable.key != it.key }.forEach {
                    rootFrame().variables()[it.key] = it.value
                }
            }.get()
        }
    }

    private fun format(str: String): String {
        return try {
            KetherFunction.parse(str, sender = adaptPlayer(player), namespace = namespaces) {
                rootFrame().variables()["@Skill"] = skill
                variables.forEach {
                    rootFrame().variables()[it.key] = it.value
                }
            }.colored()
        } catch (e: Throwable) {
            e.printKetherErrorMessage()
            "&cError: $str".colored()
        }
    }

}