package com.bh.planners.core.kether.game

import com.bh.planners.api.common.SimpleTimeoutTask
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
import taboolib.module.kether.KetherParser
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture

class ActionBlock(val material: ParsedAction<*>, val timeout: ParsedAction<*>, val selector: ParsedAction<*>?) :
    ScriptAction<List<Target>>() {

    fun execute(location: Location, material: Material, ticks: Long) {
        SimpleTimeoutTask.register(BlockSimpleTask(location, material, ticks))
    }

    override fun run(frame: ScriptFrame): CompletableFuture<List<Target>> {

        frame.runTransfer0<Material>(material) { material ->
            frame.runTransfer0<Long>(timeout) { timeout ->
                if (selector != null) {
                    frame.createContainer(selector).thenAccept {
                        submit { it.forEachLocation { execute(this, material, timeout) } }
                    }
                } else {
                    submit {
                        execute(frame.toOriginLocation()!!.value, material, timeout)
                    }
                }
            }
        }

        return CompletableFuture.completedFuture(null)
    }

    class BlockSimpleTask(val location: Location, val to: Material, tick: Long) : SimpleTimeoutTask(tick) {

        val mark = location.block.type

        override val closed: () -> Unit
            get() = { location.block.type = mark }

        init {
            location.block.type = to
        }

    }

    companion object {


        /**
         * block material timeout(tick) <selector>
         * block STONE 60 they "-@self"
         */
        @KetherParser(["block"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            ActionBlock(it.nextParsedAction(), it.nextParsedAction(), it.selectorAction())
        }

    }


}