package com.bh.planners.core.kether

import com.bh.planners.api.runVariable
import com.bh.planners.core.kether.common.CombinationKetherParser
import com.bh.planners.core.kether.common.MultipleKetherParser
import com.bh.planners.core.kether.common.KetherHelper.simpleKetherNow
import com.bh.planners.core.kether.common.KetherHelper.simpleKetherParser
import com.bh.planners.core.kether.common.SimpleKetherParser
import com.bh.planners.core.pojo.data.Data
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.*
import java.util.concurrent.CompletableFuture

@CombinationKetherParser.Used
object ActionLazyVariable : MultipleKetherParser("lazy"){

    val get = simpleKetherParser<Any?> {
        it.group(text()).apply(it) { id ->
            future { this.runVariable(id) }
        }
    }

    val reload = simpleKetherParser<Any?> {
        it.group(text()).apply(it) { id ->
            now {
                (deepVars()[id] as Optional<*>).ifPresent {
                    (it as? Data)?.toLazyGetter()?.reload()
                }
            }
        }
    }

    val main = get

    fun ScriptFrame.runVariable(id: String): CompletableFuture<Any?> {
        // 如果变量已经被加载
        return if (rootVariables().keys().contains("__${id}_VARIABLE")) {
            CompletableFuture.completedFuture(rootVariables().get<Any>("__${id}_VARIABLE").get())
        }
        // 未加载的情况 
        else {
            skill().instance.runVariable(getContext(), id).thenApply {
                this.rootVariables()["__${id}_VARIABLE"] = it
            }
        }
    }

}
