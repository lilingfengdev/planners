package com.bh.planners.core.kether

import com.bh.planners.api.EntityAPI.deleteFlag
import com.bh.planners.api.EntityAPI.getDataContainer
import com.bh.planners.api.EntityAPI.setFlag
import com.bh.planners.core.kether.common.CombinationKetherParser
import com.bh.planners.core.kether.common.ParameterKetherParser
import com.bh.planners.core.pojo.data.Data
import taboolib.module.kether.*

@CombinationKetherParser.Used
object ActionFlag : ParameterKetherParser("flag", "data") {

    val main = argumentKetherParser("get") { argument ->
        val default = nextOptionalParsedAction(arrayOf("default", "def"), "null")!!
        val selector = nextSelectorOrNull()
        actionFuture { f ->
            run(argument).str { id ->
                run(default).thenApply { default ->
                    containerOrSender(selector).thenApply { container ->
                        val entity = container.firstEntityTarget() ?: bukkitPlayer()!!
                        f.complete(entity.getDataContainer().get(id)?.data ?: default)
                    }
                }
            }
        }
    }

    val to = argumentKetherParser("set") { argument ->
        val action = nextParsedAction()
        val timeout = nextOptionalParsedAction(arrayOf("timeout","time"),-1L)!!
        val selector = nextSelectorOrNull()
        actionNow {
            run(argument).str { id ->
                run(action).thenAccept { value ->
                    run(timeout).long { timeout ->
                        containerOrSender(selector).thenAccept { container ->
                            container.forEachEntity {
                                if (value == null) {
                                    deleteFlag(id)
                                } else {
                                    setFlag(id, Data(value, survivalStamp = timeout * 50))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    val add = argumentKetherParser("add") { argument ->
        val action = this.nextParsedAction()
        val selector = nextSelectorOrNull()
        actionNow {
            run(argument).str { id ->
                run(action).thenAccept { value ->
                    containerOrSender(selector).thenAccept { container ->
                        container.forEachEntity {
                            val dataContainer = this.getDataContainer()
                            val data = dataContainer[id] ?: Data(0)
                            data.increaseAny(value ?: 0)
                            dataContainer.update(id, data)
                        }
                    }
                }
            }
        }
    }

}