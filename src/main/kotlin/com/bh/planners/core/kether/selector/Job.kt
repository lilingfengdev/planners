package com.bh.planners.core.kether.selector

import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.core.effect.Target
import com.bh.planners.core.pojo.Context
import java.util.concurrent.CompletableFuture

object Job : Selector {
    override val names: Array<String>
        get() = arrayOf("job", "!job")

    override fun check(name: String, target: Target?, args: String, context: Context, container: Target.Container): CompletableFuture<Void> {

        if (name.isNon()) {
            container.removeIf {
                (it as? Target.Entity)?.asPlayer?.plannersProfile?.job?.jobKey == args
            }
        } else {
            container.removeIf {
                (it as? Target.Entity)?.asPlayer?.plannersProfile?.job?.jobKey != args
            }
        }
        return CompletableFuture.completedFuture(null)
    }
}