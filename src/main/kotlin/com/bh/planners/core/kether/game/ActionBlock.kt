package com.bh.planners.core.kether.game

import com.bh.planners.api.common.SimpleTimeoutTask
import com.bh.planners.api.common.SimpleUniqueTask
import com.bh.planners.core.effect.Target
import com.bh.planners.core.kether.*
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import taboolib.common.platform.function.info
import taboolib.common.platform.function.submit
import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionBlock : ScriptAction<List<Target>>() {

    lateinit var material: ParsedAction<*>
    lateinit var timeout: ParsedAction<*>
    lateinit var data: ParsedAction<*>
    var selector: ParsedAction<*>? = null

    fun execute(location: Location, material: Material, data: Byte, ticks: Long) {

        // 如果上一次的任务还未结束 则提前结束
        if (cache.containsKey(location)) {
            SimpleTimeoutTask.cancel(cache[location]!!)
        }

        val simpleTask = BlockSimpleTask(location, material, data, ticks)
        SimpleTimeoutTask.register(simpleTask)
        // 注入新的
        cache[location] = simpleTask

    }

    override fun run(frame: ScriptFrame): CompletableFuture<List<Target>> {
        frame.run(material).material { material ->
            frame.run(timeout).long { timeout ->
                frame.run(data).byte { data ->
                    frame.containerOrOrigin(selector).thenAccept {
                        it.forEachLocation { execute(this, material, data, timeout) }
                    }
                }
            }
        }

        return CompletableFuture.completedFuture(null)
    }

    class BlockSimpleTask(val location: Location, var to: Material, val data: Byte, tick: Long) : SimpleTimeoutTask(tick) {

        val world = location.world!!
        var block = location.block
        var mark = location.block.type

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

    companion object {

        val cache = mutableMapOf<Location, BlockSimpleTask>()

        /**
         * block material timeout(tick) <selector>
         * block STONE 60 they "-@self"
         */
        @KetherParser(["block"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            val actionBlock = ActionBlock()
            actionBlock.material = it.nextParsedAction()
            actionBlock.timeout = it.nextParsedAction()
            actionBlock.data = it.tryGet(arrayOf("data"), "0")!!
            actionBlock.selector = it.nextSelectorOrNull()
            actionBlock
        }

    }


}