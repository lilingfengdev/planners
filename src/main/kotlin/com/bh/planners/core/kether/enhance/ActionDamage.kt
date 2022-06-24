package com.bh.planners.core.kether.enhance

import com.bh.planners.core.kether.*
import taboolib.common.platform.function.info
import taboolib.common.platform.function.submit
import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionDamage {

    class Damage(val value: ParsedAction<*>, val selector: ParsedAction<*>) : ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            return frame.newFrame(value).run<Any>().thenAccept { damage ->
                frame.createTargets(selector).thenAccept {
                    it.forEachEntity {
                        this.damage(Coerce.toDouble(damage))
                    }
                }

            }
        }

    }

    class Attack(val value: ParsedAction<*>, val selector: ParsedAction<*>) : ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            return frame.newFrame(value).run<Any>().thenAccept { damage ->
                val asPlayer = frame.asPlayer() ?: return@thenAccept
                frame.createTargets(selector).thenAccept {
                    submit(async = false) {
                        it.forEachEntity {
                            this.damage(Coerce.toDouble(damage), asPlayer)
                        }
                    }

                }

            }
        }

    }

    companion object {

        /**
         * 对selector目标造成伤害
         * damage [damage] [selector]
         * damage 10.0 "-@aline 10"
         */
        @KetherParser(["damage"], namespace = NAMESPACE)
        fun parser() = scriptParser {
            Damage(it.next(ArgTypes.ACTION), it.next(ArgTypes.ACTION))
        }

        /**
         * 对selector目标攻击,
         * attack [damage] [selector]
         * attack 10.0 "-@aline 10"
         */
        @KetherParser(["attack"], namespace = NAMESPACE)
        fun parser2() = scriptParser {
            Attack(it.next(ArgTypes.ACTION), it.next(ArgTypes.ACTION))
        }

    }

}