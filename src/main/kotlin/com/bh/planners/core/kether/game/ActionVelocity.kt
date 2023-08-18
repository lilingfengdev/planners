package com.bh.planners.core.kether.game

import com.bh.planners.core.effect.Target
import com.bh.planners.core.kether.common.CombinationKetherParser
import com.bh.planners.core.kether.common.KetherHelper
import com.bh.planners.core.kether.common.KetherHelper.containerOrSender
import com.bh.planners.core.kether.common.MultipleKetherParser
import org.bukkit.entity.LivingEntity
import org.bukkit.util.Vector
import taboolib.module.kether.ScriptFrame

@CombinationKetherParser.Used
object ActionVelocity : MultipleKetherParser("velocity") {

    @CombinationKetherParser.Ignore
    fun actionParser(block: ScriptFrame.(Vector, Target.Container) -> Unit) = KetherHelper.simpleKetherParser<Unit>() {
        it.group(double(), double(), double(), containerOrSender()).apply(it) { x, y, z, container ->
            now {
                block(this, Vector(x, y, z), container)
            }
        }
    }

    val add =
        actionParser { vector, container ->
            container.forEachLivingEntity {
                generateVelocity { add(vector) }
            }
        }

    val sub =
        actionParser { vector, container ->
            container.forEachLivingEntity {
                generateVelocity { subtract(vector) }
            }
        }

    val mul =
        actionParser { vector, container ->
            container.forEachLivingEntity {
                generateVelocity { multiply(vector) }
            }
        }

    val div =
        actionParser { vector, container ->
            container.forEachLivingEntity {
                generateVelocity { divide(vector) }
            }
        }

    val set =
        actionParser { vector, container ->
            container.forEachLivingEntity {
                generateVelocity {
                    this.x = vector.x
                    this.y = vector.y
                    this.z = vector.z
                }
            }
        }

    @CombinationKetherParser.Ignore
    fun LivingEntity.generateVelocity(block: Vector.() -> Unit) {
        this.velocity = velocity.apply(block)
    }

}