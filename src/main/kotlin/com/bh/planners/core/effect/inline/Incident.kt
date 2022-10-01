package com.bh.planners.core.effect.inline

import com.bh.planners.api.script.ScriptLoader
import com.bh.planners.core.pojo.Session
import taboolib.module.kether.ScriptContext

interface Incident {

    fun inject(context: ScriptContext)

    companion object {

        @JvmName("handleIncident1")
        fun Session.handleIncident(name: String, incident: Incident) {
            handleIncident(this, name, incident)
        }

        fun handleIncident(session: Session, name: String, incident: Incident) {
            ScriptLoader.invokeFunction(session, name) {
                incident.inject(it)
            }
        }

    }


}