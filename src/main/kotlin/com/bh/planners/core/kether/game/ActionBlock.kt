package com.bh.planners.core.kether.game

import com.bh.planners.api.common.SimpleTimeoutTask
import com.bh.planners.core.effect.Target
import com.bh.planners.core.kether.NAMESPACE
import com.bh.planners.core.kether.execLocation
import com.bh.planners.core.kether.selectorAction
import com.bh.planners.core.kether.toOriginLocation
import org.bukkit.Location
import org.bukkit.Material
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
        val oldMaterial = location.block.type
        location.block.type = material
        // 销毁
        SimpleTimeoutTask.createSimpleTask(ticks, async = false) {
            location.block.type = oldMaterial
        }
    }

    override fun run(frame: ScriptFrame): CompletableFuture<List<Target>> {

        val future = CompletableFuture<List<Target>>()
        frame.newFrame(material).run<Any>().thenAccept {
            val material = Coerce.toEnum(it.toString().uppercase(), Material::class.java)
            frame.newFrame(timeout).run<Any>().thenAccept {
                val ticks = Coerce.toLong(it)
                if (selector != null) {
                    frame.execLocation(selector) { execute(this, material, ticks) }
                } else {
                    execute(frame.toOriginLocation()!!.value, material, ticks)
                }

            }
        }

        return future
    }

    companion object {


        /**
         * block material timeout(tick) <selector>
         * block STONE 6000 they "-@self"
         */
        @KetherParser(["block"], namespace = NAMESPACE)
        fun parser() = scriptParser {
            ActionBlock(it.next(ArgTypes.ACTION), it.next(ArgTypes.ACTION), it.selectorAction())
        }

    }


}