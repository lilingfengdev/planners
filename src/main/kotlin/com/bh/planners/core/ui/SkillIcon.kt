package com.bh.planners.core.ui

import com.bh.planners.api.PlannersAPI
import com.bh.planners.api.common.Translator
import com.bh.planners.api.script.ScriptLoader
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.pojo.Context
import com.bh.planners.core.pojo.player.PlayerJob
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.library.xseries.XItemStack
import taboolib.module.chat.colored
import taboolib.module.configuration.Configuration
import taboolib.module.kether.runKether
import taboolib.module.nms.getItemTag
import taboolib.platform.util.buildItem

class SkillIcon(val player: Player, skillKey: String, val level: Int, conceal: Boolean = false) {

    companion object {

        fun PlayerJob.Skill.toIcon(player: Player, conceal: Boolean = false): SkillIcon {
            return toIcon(player, this.level, conceal)
        }

        fun PlayerJob.Skill.toIcon(player: Player, level: Int, conceal: Boolean = false): SkillIcon {
            return SkillIcon(player, key, level, conceal)
        }

        fun PlayerJob.Skill.buildIconItem(player: Player): ItemStack {
            return toIcon(player).build()
        }

    }

    private val skill = PlannersAPI.getSkill(skillKey)!!

    val context = Context.Impl1(player.toTarget(), skill, level)

    private val option = skill.option

    val icon = option.root.getConfigurationSection("icon") ?: Configuration.empty()

    val iconNBT = icon.getConfigurationSection("nbt") ?: Configuration.empty()

    fun build(): ItemStack {
        val item = buildItem(XItemStack.deserialize(icon)) {
            name = format(name ?: "")
            lore.forEachIndexed { index, s -> lore[index] = format(s) }
        }
        val itemTag = item.getItemTag()
        Translator.toNBTBase(iconNBT)!!.asCompound().forEach {
            itemTag[it.key] = it.value
        }
        itemTag.saveTo(item)
        return item
    }

    fun format(str: String): String {
        return runKether {
            ScriptLoader.createFunctionScript(context, str).colored()
        } ?: "&cError: $str".colored()
    }

}