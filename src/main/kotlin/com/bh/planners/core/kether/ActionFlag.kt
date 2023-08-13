package com.bh.planners.core.kether

import com.bh.planners.api.EntityAPI.deleteFlag
import com.bh.planners.api.EntityAPI.getDataContainer
import com.bh.planners.api.EntityAPI.setFlag
import com.bh.planners.core.kether.common.CombinationKetherParser
import com.bh.planners.core.kether.common.ParameterKetherParser
import com.bh.planners.core.kether.common.KetherHelper.containerOrSender
import com.bh.planners.core.pojo.data.Data
import taboolib.module.kether.combinationParser

@CombinationKetherParser.Used
object ActionFlag : ParameterKetherParser("flag", "data") {

    val get = combinationParser {
        it.group(command("default", "def", then = any()).option(), containerOrSender()).apply(it) { default, container ->
            argumentNow { id ->
                val entity = container.firstEntityTarget() ?: bukkitPlayer()!!
                entity.getDataContainer().get(id.toString())?.data ?: default
            }
        }
    }

    val other = get

    val to = combinationParser {
        it.group(any(), command("timeout", then = long()).option().defaultsTo(-1), containerOrSender()).apply(it) { value, tick, container ->
            argumentNow { id ->
                container.forEachEntity {
                    if (value == null) {
                        deleteFlag(id.toString())
                    } else {
                        setFlag(id.toString(), Data(value, survivalStamp = tick * 50L))
                    }
                }
            }
        }
    }

    val add = combinationParser {
        it.group(any(), command("timeout", then = long()).option().defaultsTo(-1), containerOrSender()).apply(it) { value, tick, container ->
            argumentNow { id ->
                container.forEachEntity {
                    if (value == null) {
                        deleteFlag(id.toString())
                    } else {
                        setFlag(id.toString(), Data(value, survivalStamp = tick * 50L))
                    }
                }
            }
        }
    }

}