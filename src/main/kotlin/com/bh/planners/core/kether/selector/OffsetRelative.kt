package com.bh.planners.core.kether.selector

import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.pojo.Context
import taboolib.common5.Coerce
import java.util.concurrent.CompletableFuture

/**
 * 该操作会把实体目标转换为坐标目标
 */
object OffsetRelative : Selector{
    override val names: Array<String>
        get() = arrayOf("offset-r","offsetr","offset-relative")

    override fun check(
        name: String,
        target: Target?,
        args: String,
        context: Context,
        container: Target.Container
    ): CompletableFuture<Void> {
        val offset = if (args.contains(",")) args.split(",") else listOf(args, args, args)
        val split = offset.map { Coerce.toDouble(it) }

        val removes = mutableListOf<Target>()
        val addons = mutableListOf<Target>()

        container.targets.forEach {
            if (it is Target.Entity) {
                removes += it
                addons += it.value.clone().add(split[0],split[1],split[2]).toTarget()
            } else if (it is Target.Location) {
                it.value.add(split[0],split[1],split[2])
            }
        }

        container.removeIf { this in removes }
        container.addAll(addons)

        return CompletableFuture.completedFuture(null)
    }
}