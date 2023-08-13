package com.bh.planners.api

import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.core.pojo.player.PlayerJob
import com.bh.planners.core.ui.ShortcutSelector
import org.bukkit.entity.Player
import taboolib.platform.util.sendLang

object ProfileAPI {

    /**
     * 呼出玩家的技能快捷栏绑定器
     */
    fun openSkillShortcutBinder(player: Player,skill: PlayerJob.Skill, call: Runnable) {
        ShortcutSelector(player) {
            // 取消绑定
            if (skill.shortcutKey == it.key) {
                player.sendLang("skill-un-bind-shortcut", skill.instance.option.name)
            } else {
                player.sendLang("skill-bind-shortcut", skill.instance.option.name, it.name)
            }
            player.plannersProfile.bind(skill, it)
            call.run()
        }.open()
    }

}