package com.bh.planners.core.kether

import taboolib.common.platform.function.info
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionOrigin {

    class SetOriginLocation(val action: ParsedAction<*>) : ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {

            frame.newFrame(action).run<String>().thenAccept {
                frame.rootVariables()["@Origin"] = it.toLocation()
            }
            return CompletableFuture.completedFuture(null)
        }

    }

    companion object {

        /**
         *
         * 设置技能释放原点
         * origin get
         *
         */
        @KetherParser(["origin"], namespace = NAMESPACE)
        fun parser() = scriptParser {
            it.switch {
                case("set") {
                    SetOriginLocation(it.next(ArgTypes.ACTION))
                }
                other {
                    actionNow {
                        toOriginLocation()?.value ?: "__empty__"
                    }
                }
            }
        }
    }


}