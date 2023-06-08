package com.bh.planners.core.kether.compat.adyeshach

import com.bh.planners.api.entity.ProxyAdyeshachEntity
import com.bh.planners.core.effect.Target
import com.bh.planners.core.kether.*
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*

object ActionAdyeshach {


    /**
     * adyeshach spawn type name tick
     * adyeshach follow <option: action> [owner:first] [selector:entity]
     *
     * adyeshach script file args[] selector
     *
     * adyeshach remove [selector]
     *
     */
    @KetherParser(["adyeshach", "ady"], namespace = NAMESPACE, shared = true)
    fun parser() = scriptParser {
        it.switch {
            case("spawn") {
                ActionAdyeshachSpawn().apply {
                    this.type = it.nextParsedAction()
                    this.name = it.nextParsedAction()
                    this.timeout = it.nextParsedAction()
                    this.selector = it.nextSelectorOrNull()
                }
            }
            case("follow") {
                ActionAdyeshachFollow(
                    it.nextParsedAction(),
                    it.nextParsedAction(),
                    it.nextArgumentAction(arrayOf("option", "params"), "EMPTY")!!
                )
            }
            case("script") {
                ActionAdyeshachScript(
                    it.nextParsedAction(),
                    it.next(ArgTypes.listOf(ArgTypes.ACTION)),
                    it.nextSelectorOrNull()
                )
            }
            case("remove") {
                ActionAdyeshachRemove(it.nextSelector())
            }
        }

    }

    fun Target.Container.foreachAdyEntity(block: ProxyAdyeshachEntity.() -> Unit) {
        this.forEach<Target.Entity> {
            if (this.proxy is ProxyAdyeshachEntity) {
                block(this.proxy)
            }
        }
    }

    fun ScriptFrame.execAdyeshachEntity(selector: ParsedAction<*>, call: ProxyAdyeshachEntity.() -> Unit) {
        exec(selector) {
            if (this is Target.Entity && this.proxy is ProxyAdyeshachEntity) {
                call(this.proxy)
            }
        }
    }


}