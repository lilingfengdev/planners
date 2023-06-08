package com.bh.planners.api.compat.origin

import ac.github.oa.internal.core.condition.LevelCondition
import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.PlannersAPI.plannersProfileIsLoaded
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import java.util.function.Function

object OriginConditionLevel {

    @Awake(LifeCycle.ENABLE)
    fun init() {
        if (OriginScript.isEnable) {
            LevelCondition.check = Function {
                if (it.plannersProfileIsLoaded) {
                    it.plannersProfile.level
                } else it.level
            }
        }
    }

}