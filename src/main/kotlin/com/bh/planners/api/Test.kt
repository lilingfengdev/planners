package com.bh.planners.api

import com.bh.planners.core.effect.Target

object Test {

    @JvmStatic
    fun main(args: Array<String>) {
        ContextAPI.createSession(Shit(),PlannersAPI.skills.first())
    }

    class Shit : Target {
        override fun toLocal(): String {
            return "臭粑粑"
        }
    }

}