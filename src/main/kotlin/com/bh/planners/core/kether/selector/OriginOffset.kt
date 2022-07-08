package com.bh.planners.core.kether.selector

import com.bh.planners.core.pojo.Context
import com.bh.planners.core.skill.effect.Target
import com.bh.planners.core.skill.effect.Target.Companion.toTarget
import taboolib.common5.Coerce
import java.util.concurrent.CompletableFuture

object OriginOffset : Selector {

    override val names: Array<String>
        get() = arrayOf("offset")

    override fun check(
        name: String,
        target: Target?,
        args: String,
        context: Context,
        container: Target.Container
    ): CompletableFuture<Void> {
        val location = target as? Target.Location ?: return CompletableFuture.completedFuture(null)
        val offset = if (args.contains(",")) args.split(",") else listOf(args, args, args)
        val split = offset.map { Coerce.toDouble(it) }
        container.add(location.value.clone().add(split[0], split[1], split[2]).toTarget())
        return CompletableFuture.completedFuture(null)
    }
}