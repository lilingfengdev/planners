package com.bh.planners.api.compat.chemdah

import ink.ptms.chemdah.api.ChemdahAPI
import ink.ptms.chemdah.core.quest.QuestLoader.register
import ink.ptms.chemdah.core.quest.objective.Objective
import ink.ptms.chemdah.core.quest.objective.ObjectiveCountableI
import org.bukkit.Bukkit
import taboolib.common.LifeCycle
import taboolib.common.io.getInstance
import taboolib.common.io.runningClasses
import taboolib.common.platform.Awake
import taboolib.common.platform.function.info
import taboolib.module.chat.colored

object ChemdahHook {

    // 禁止哪些任务加载,根据注解参数来判断
    private fun listQuest(): List<String> {
        return listOf()
    }

    @Awake(LifeCycle.ENABLE)
    fun reg() {
        runningClasses.forEach {
            if (Objective::class.java.isAssignableFrom(it) && it.isAnnotationPresent(LoadQuest::class.java)) {
                val result = it.getAnnotation(LoadQuest::class.java)
                if (listQuest().contains(result.questName)) return@forEach
                (it.getInstance()?.get() as ObjectiveCountableI<*>).register()
            }
        }
//        if (Bukkit.getPluginManager().isPluginEnabled("Chemdah")) {
//            reg(
//                listOf<ObjectiveCountableI<*>>(
//                    PreSkillCast,
//                    PostSkillCast,
//                    RecordSkillCast,
//                    FailureSkillCast,
//                    PlayerSelectedJob,
//                    PlayerSkillUpgrade,
//                    PlayerTransfer,
//                    PlayerSkillBind,
//                    PlayerKeyDown,
//                    PlayerLevelChange
//                )
//            )
//            //重载
//            ChemdahAPI.reloadAll()
//        }
    }

//    fun reg(cs: List<ObjectiveCountableI<*>>) {
//        cs.forEach {
//            it.register()
//            info("&6CHEM拓展注册 ${it.name} &a成功".colored())
//        }
//    }

}