package com.bh.planners.core.effect.inline

import com.bh.planners.api.script.ScriptLoader
import com.bh.planners.core.pojo.Context
import com.bh.planners.core.pojo.Session
import taboolib.module.kether.ScriptContext
import javax.xml.transform.Source

interface Incident {

    fun inject(context: ScriptContext)

    companion object {

        @JvmName("handleIncident1")
        fun Context.SourceImpl.handleIncident(name: String, incident: Incident) {
            handleIncident(this, name, incident)
        }

        fun handleIncident(context: Context.SourceImpl, name: String, incident: Incident) {
            ScriptLoader.invokeFunction(context, name) {
                incident.inject(it)
            }
        }

    }


}