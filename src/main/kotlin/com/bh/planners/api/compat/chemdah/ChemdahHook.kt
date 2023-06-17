package com.bh.planners.api.compat.chemdah

import ink.ptms.chemdah.api.ChemdahAPI
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
        if (Bukkit.getPluginManager().isPluginEnabled("Chemdah")) {
            reg(
                listOf<ObjectiveCountableI<*>>(
                    PreSkillCast,
                    PostSkillCast,
                    RecordSkillCast,
                    FailureSkillCast,
                    PlayerSelectedJob,
                    PlayerSkillUpgrade,
                    PlayerTransfer,
                    PlayerSkillBind
                )
            )
            //重载
            ChemdahAPI.reloadAll()
        }
    }

    fun reg(cs : List<ObjectiveCountableI<*>>) {
        cs.forEach {
            it.register()
            info("&6CHEM拓展注册 ${it.name} &a成功".colored())
        }
    }

}