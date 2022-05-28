package com.bh.planners.core.kether

import com.bh.planners.api.particle.Demand
import com.bh.planners.core.kether.effect.Target.Companion.createContainer
import org.bukkit.entity.Player
import taboolib.common.platform.function.info
import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionDamage {

    class Attack(val value: ParsedAction<*>, val event: ParsedAction<*>, val selector: ParsedAction<*>) :
        ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {

            frame.newFrame(value).run<String>().thenAccept { damage ->
                frame.newFrame(event).run<String>().thenAccept { event ->
                    frame.newFrame(selector).run<String>().thenAccept { selector ->
                        val player = frame.script().sender!!.cast<Player>()
                        Demand(selector).createContainer(player, frame.getSession()).forEachEntity {
                            if (Coerce.toBoolean(event)) {
                                this.damage(Coerce.toDouble(damage), player)
                            } else {
                                this.damage(Coerce.toDouble(damage))
                            }
                        }
                    }
                }

            }

            return CompletableFuture.completedFuture(null)
        }

    }


    companion object {

        /**
         * 对selector目标造成伤害,event为true则有伤害来源,反之绝对s伤害
         * damage [damage] [event] [selector]
         * damage 10.0 true "-@aline 10"
         */
        @KetherParser(["damage"], namespace = NAMESPACE)
        fun parser() = scriptParser {
            val damage = it.next(ArgTypes.ACTION)
            Attack(damage, it.next(ArgTypes.ACTION), it.next(ArgTypes.ACTION))
        }

    }

}