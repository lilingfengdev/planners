package com.bh.planners.core.kether.game

import com.bh.planners.api.entity.ProxyEntity
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


    fun actionParser(block: ScriptFrame.(Vector, Target.Container) -> Unit) = KetherHelper.simpleKetherParser<Unit>() {
        it.group(double(), double(), double(), containerOrSender()).apply(it) { x, y, z, container ->
            now {
                block(this, Vector(x, y, z), container)
            }
        }
    }

    val add = actionParser { vector, container ->
        container.forEachLivingEntity {
            generatedVelocity { add(vector) }
        }
    }

    val sub = actionParser { vector, container ->
        container.forEachLivingEntity {
            generatedVelocity { subtract(vector) }
        }
    }

    val mul = actionParser { vector, container ->
        container.forEachLivingEntity {
            generatedVelocity { multiply(vector) }
        }
    }

    val div = actionParser { vector, container ->
        container.forEachLivingEntity {
            generatedVelocity { divide(vector) }
        }
    }

    val set = actionParser { vector, container ->
        container.forEachLivingEntity {
            generatedVelocity {
                this.x = vector.x
                this.y = vector.y
                this.z = vector.z
            }
        }
    }

    fun LivingEntity.generatedVelocity(block: Vector.() -> Unit) {
        this.velocity = velocity.clone().apply(block)
    }

    fun ProxyEntity.generatedVelocity(block: Vector.() -> Unit) {
        this.velocity = velocity.clone().apply(block)
    }

}