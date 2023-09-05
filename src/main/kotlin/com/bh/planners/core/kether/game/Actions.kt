package com.bh.planners.core.kether.game

import com.bh.planners.core.effect.safeDistance
import com.bh.planners.core.kether.common.CombinationKetherParser
import com.bh.planners.core.kether.common.KetherHelper
import com.bh.planners.core.kether.common.KetherHelper.actionContainer
import com.bh.planners.core.kether.common.KetherHelper.actionContainerOrOrigin
import com.bh.planners.core.kether.common.KetherHelper.containerOrOrigin
import com.bh.planners.core.kether.common.KetherHelper.containerOrSender
import com.bh.planners.core.kether.getContext
import com.bh.planners.core.kether.origin
import com.bh.planners.core.kether.parseTargetContainer
import org.bukkit.Sound
import org.bukkit.util.Vector
import taboolib.common5.Coerce
import taboolib.module.kether.ParserHolder.option
import taboolib.platform.util.sendActionBar
import java.util.*

@CombinationKetherParser.Used
fun actionbar() = KetherHelper.simpleKetherParser<Unit> {
    it.group(text(), containerOrSender()).apply(it) { text, container ->
        now {
            container.forEachPlayer { sendActionBar(text) }
        }
    }
}

@CombinationKetherParser.Used
fun title() = KetherHelper.simpleKetherParser<Unit> {
    it.group(
        text(),
        command("subtitle", then = text()).option(),
        command("by", "with", then = int().and(int(), int())).option().defaultsTo(Triple(0, 20, 0)),
        containerOrSender()
    ).apply(it) { title, subtitle, with, container ->
        now {
            container.forEachPlayer {
                sendTitle(title, subtitle ?: "", with.first, with.second, with.third)
            }
        }
    }
}

@CombinationKetherParser.Used
fun tell() = KetherHelper.simpleKetherParser<Unit>("send", "message") {
    it.group(text(), containerOrSender()).apply(it) { message, container ->
        now { container.forEachPlayer { sendMessage(message) } }
    }
}

@CombinationKetherParser.Used
fun teleport() = KetherHelper.simpleKetherParser<Unit>("tp") {
    it.group(any(), containerOrSender()).apply(it) { loc, container ->
        now {
            val location = parseTargetContainer(loc!!, getContext()).firstBukkitLocation()!!
            container.forEachPlayer { teleport(location) }
        }
    }
}

/**
 * 播放音效
 */
@CombinationKetherParser.Used
fun sound() = KetherHelper.simpleKetherParser<Unit> {
    it.group(
        text(),
        command("by", "with", then = float().and(float())).option().defaultsTo(1f to 1f),
        containerOrSender()
    ).apply(it) { sound, with, container ->
        now {
            val (volume, pitch) = with
            container.forEachPlayer {
                if (sound.startsWith("resource:")) {
                    playSound(location, sound.substring("resource:".length), volume, pitch)
                } else {
                    playSound(location, Sound.valueOf(sound.replace('.', '_').uppercase(Locale.getDefault())), volume, pitch)
                }
            }
        }
    }
}


/**
 * 使目标点燃
 * fireTicks ticks <selector: action(sender)>
 */
@CombinationKetherParser.Used
fun fireTicks() = KetherHelper.simpleKetherParser<Unit>("fireticks", "fire-ticks") {
    it.group(int(), containerOrSender()).apply(it) { tick, container ->
        now { container.forEachLivingEntity { fireTicks = tick } }
    }
}

/**
 * 使目标冻结
 * freezeTicks ticks <selector: action(sender)>
 */
@CombinationKetherParser.Used
fun freezeTicks() = KetherHelper.simpleKetherParser<Unit>("freezeticks", "freeze-ticks") {
    it.group(int(), containerOrSender()).apply(it) { tick, container ->
        now { container.forEachLivingEntity { freezeTicks = tick } }
    }
}


/**
 * 在指定(目标)坐标处召唤一次爆炸
 * explosion power <selector: (action)>
 */
@CombinationKetherParser.Used
fun explosion() = KetherHelper.simpleKetherParser<Unit> {
    it.group(float(), containerOrOrigin()).apply(it) { power, container ->
        now {
            container.forEachLocation {
                world!!.createExplosion(x, y, z, power, false, false)
            }
        }
    }
}

/**
 * 在指定地点打雷
 * lightning <selector: (action)>
 */
@CombinationKetherParser.Used
fun lightning() = KetherHelper.simpleKetherParser<Unit> {
    it.group(containerOrOrigin()).apply(it) { container ->
        now {
            container.forEachLocation {
                world!!.strikeLightningEffect(this)
            }
        }
    }
}
/**
 * 拖拽目标
 * drag power <pos> <selector: action(origin)>
 */
@CombinationKetherParser.Used
fun drag() = KetherHelper.simpleKetherParser<Unit>("drag") {
    it.group(double(), actionContainer(), command("pos", then = actionContainerOrOrigin()).option()).apply(it) { step, container, target ->
        now {
            val location = target?.firstBukkitLocation() ?: origin().value
            container.forEachProxyEntity {
                val vectorAB = this.location.clone().subtract(location).toVector()
                vectorAB.normalize()
                vectorAB.multiply(step)
                this.velocity = vectorAB
            }
        }
    }
}


/**
 * 为目标添加一个基于视角方向的向量 (即冲刺)
 * launch x y z <selector: action(origin)>
 * launch -2 0.5 0 they "@self"  -  使自己向后跳跃
 * launch 2 0.5 0 they "@self"   -  使自己向前跳跃
 */
@CombinationKetherParser.Used
fun launch() = KetherHelper.simpleKetherParser<Unit> {
    it.group(double(), double(), double(), containerOrOrigin()).apply(it) { x, y, z, container ->
        now {
            container.forEachProxyEntity {
                val vector1 = this.location.direction.setY(0).normalize()
                val vector2 = vector1.clone().crossProduct(Vector(0, 1, 0))
                vector1.multiply(Coerce.toDouble(x))
                vector1.add(vector2.multiply(Coerce.toDouble(z))).y = Coerce.toDouble(y)
                this.velocity = vector1
            }
        }
    }
}

/**
 * safe-distance selector1 selector2(1)
 * 计算两点安全距离
 */
@CombinationKetherParser.Used
fun safeDistance() = KetherHelper.simpleKetherParser<Unit>("long","safe-distance") {
    it.group(actionContainerOrOrigin(), containerOrOrigin()).apply(it) { p1, p2 ->
        now {
            val loc1 = p1.firstBukkitLocation() ?: p1.firstProxyEntity()?.location ?: return@now
            val loc2 = p2.firstBukkitLocation() ?: p2.firstProxyEntity()?.location ?: return@now
            loc1.safeDistance(loc2)
        }
    }
}