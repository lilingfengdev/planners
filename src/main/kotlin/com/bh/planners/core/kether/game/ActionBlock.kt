package com.bh.planners.core.kether.game

import com.bh.planners.api.common.SimpleTimeoutTask
import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.target
import com.bh.planners.core.kether.*
import com.bh.planners.core.kether.common.CombinationKetherParser
import com.bh.planners.core.kether.common.KetherHelper
import com.bh.planners.core.kether.common.KetherHelper.containerOrOrigin
import com.bh.planners.core.kether.common.KetherHelper.materialOrStone
import org.bukkit.Location
import org.bukkit.Material
import taboolib.common5.cbyte
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

private val blocks = mutableMapOf<Location, BlockSimpleTask>()

/**
 * 添加方块蒙板
 * block material tick <data: action(0)> <selector: action(origin)>
 */
@CombinationKetherParser.Used
fun actionBlock() = KetherHelper.simpleKetherParser<Target.Container>("block") {
    it.group(
            materialOrStone(), long(), command("data", then = int()).option().defaultsTo(0), containerOrOrigin()
    ).apply(it) { material, tick, data, container ->
        now {
            createTargetContainerDSL { result ->
                container.forEachLocation {
                    // 注销上次的任务
                    if (blocks.containsKey(this)) {
                        SimpleTimeoutTask.cancel(blocks[this]!!)
                    }
                    // 添加新的任务
                    val task = BlockSimpleTask(this, material, data.cbyte, tick)
                    result += Target.Location(this)
                    SimpleTimeoutTask.register(task)
                    blocks[this] = task
                }
            }
        }
    }
}

private class BlockSimpleTask(val location: Location, var to: Material, val data: Byte, tick: Long) : SimpleTimeoutTask(tick) {

    val world = location.world!!
    var block = location.block

    override val closed: () -> Unit
        get() = {
            update()
            isClosed = true
        }

    fun update() {

        world.players.forEach {
            it.sendBlockChange(location, block.type, block.data)
        }

    }

    init {
        world.players.forEach {
            it.sendBlockChange(location, to, data)
        }
    }

}
