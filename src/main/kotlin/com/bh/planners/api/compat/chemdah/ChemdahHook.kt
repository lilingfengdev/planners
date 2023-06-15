package com.bh.planners.api.compat.chemdah

import ink.ptms.chemdah.core.quest.QuestLoader.register
import ink.ptms.chemdah.core.quest.objective.ObjectiveCountableI
import org.bukkit.Bukkit
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.function.info
import taboolib.module.chat.colored

object ChemdahHook {

    @Awake(LifeCycle.ENABLE)
    fun reg() {
        if (Bukkit.getPluginManager().isPluginEnabled("chemdah")) {
            reg(PlayerSelectedJob)
            reg(PlayerCastSkill)
            reg(PlayerSkillUpgrade)
            reg(PlayerSkillBind)
            reg(PlayerTransfer)
        }
    }

    fun reg(cs : ObjectiveCountableI<*>) {
        cs.register()
        info("&6CHEM拓展注册 ${cs.name} &a成功".colored())
    }

}