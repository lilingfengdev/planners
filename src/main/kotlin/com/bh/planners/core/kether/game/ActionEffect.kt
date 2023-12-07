package com.bh.planners.core.kether.game

import com.bh.planners.core.effect.EffectOption
import com.bh.planners.core.effect.Effects
import com.bh.planners.core.effect.Target.Companion.getLocation
import com.bh.planners.core.effect.common.EffectSpawner
import com.bh.planners.core.effect.custom.EffectWing
import com.bh.planners.core.effect.inline.Incident.Companion.handleIncident
import com.bh.planners.core.effect.inline.IncidentEffectHit
import com.bh.planners.core.effect.inline.IncidentEffectTick
import com.bh.planners.core.kether.*
import com.bh.planners.util.entityAt
import taboolib.common.platform.function.adaptLocation
import taboolib.library.kether.ParsedAction
import taboolib.module.effect.EffectGroup
import taboolib.module.effect.shape.NRankBezierCurve
import taboolib.module.effect.shape.Ray
import taboolib.module.kether.*
import kotlin.collections.set

object ActionEffect {

    /**
     * 创建一个特定类型的key效果
     * effect create [key: String] [type: String]
     * 创建一个key效果的类型
     * effect type [key: String] [type: String]
     * 获取一个key效果
     * effect get [key: String]
     */
    @KetherParser(["effect"], namespace = NAMESPACE, shared = true)
    fun effect() = scriptParser {
        it.switch {
            case("create") {
                val key = it.nextToken()
                val type = it.nextToken()

                actionNow {
                    val option = EffectOption(type)
                    rootVariables().set("effect_$key", option)
                    option
                }
            }

            case("type") {
                val key = it.nextToken()
                val type = it.nextToken()

                actionNow {
                    val old = rootVariables().get<EffectOption>("effect_$key").get()
                    val oldType = old.type
                    rootVariables().set("effect_$key", old.also { it.type = type })
                    oldType
                }
            }

            case("get") {
                val key = it.nextToken()
                actionNow {
                    rootVariables().get<EffectOption>("effect_$key").get()
                }
            }

        }

    }

    /**
     * option [key: String] [option: String] [data: String]
     * 修改key效果参数的语句
     * */
    @KetherParser(["option"], namespace = NAMESPACE, shared = true)
    fun effectOption() = combinationParser {
        it.group(
            text(),
            text(),
            text()
        ).apply(it) { key, option, info ->
            now {
                val effectOption = rootVariables().get<EffectOption>("effect_$key").get()
                effectOption.options[option] = EffectOption.Option(info)
                rootVariables().set("effect_$key", effectOption)
            }
        }
    }


    /**
     * show a,b,c scale 1.2 rotateX 10 rotateY 10 rotateZ 10 origin they "@self" they "@range 10" onHit hit onTick tick
     * 一瞬间展示粒子
     * */
    @KetherParser(["show"], namespace = NAMESPACE)
    fun show() = combinationParser {
        it.group(
            text(),
            command("scale", then = double()).option(),
            command("rotateX", then = double()).option(),
            command("rotateY", then = double()).option(),
            command("rotateZ", then = double()).option(),
            command("origin", then = action()).option(),
            command("they", then = action()).option(),
            command("onHit", then = text()).option(),
            command("onTick", then = text()).option()
        ).apply(it) { keys, scale, rotateX, rotateY, rotateZ, origin, selector, onHit, onTick ->
            now {
                showEffect(Type.SHOW, keys, scale, rotateX, rotateY, rotateZ, origin, selector, onHit, onTick)
            }
        }

    }

    /**
     * play a,b,c scale 1.2 rotateX 10 rotateY 10 rotateZ 10 origin they "@self" they "@range 10" onHit hit onTick tick
     * 绘画粒子
     * */
    @KetherParser(["play"], namespace = NAMESPACE)
    fun play() = combinationParser {
        it.group(
            text(),
            command("scale", then = double()).option(),
            command("rotateX", then = double()).option(),
            command("rotateY", then = double()).option(),
            command("rotateZ", then = double()).option(),
            command("origin", then = action()).option(),
            command("they", then = action()).option(),
            command("onHit", then = text()).option(),
            command("onTick", then = text()).option()
        ).apply(it) { keys, scale, rotateX, rotateY, rotateZ, origin, selector, onHit, onTick ->
            now {
                showEffect(Type.PLAY, keys, scale, rotateX, rotateY, rotateZ, origin, selector, onHit, onTick)
            }
        }

    }


    /**
     * alwaysshow a,b,c scale 1.2 rotateX 10 rotateY 10 rotateZ 10 origin they "@self" they "@range 10" onHit hit onTick tick
     * 每tick展示一次全部粒子
     * */
    @KetherParser(["alwaysshow"], namespace = NAMESPACE)
    fun alwaysshow() = combinationParser {
        it.group(
            text(),
            command("scale", then = double()).option(),
            command("rotateX", then = double()).option(),
            command("rotateY", then = double()).option(),
            command("rotateZ", then = double()).option(),
            command("origin", then = action()).option(),
            command("they", then = action()).option(),
            command("onHit", then = text()).option(),
            command("onTick", then = text()).option()
        ).apply(it) { keys, scale, rotateX, rotateY, rotateZ, origin, selector, onHit, onTick ->
            now {
                showEffect(Type.ALWAYS_SHOW, keys, scale, rotateX, rotateY, rotateZ, origin, selector, onHit, onTick)
            }
        }

    }

    /**
     * alwaysplay a,b,c scale 1.2 rotateX 10 rotateY 10 rotateZ 10 origin they "@self" they "@range 10" onHit hit onTick tick
     * 每tick开始一次绘画
     * */
    @KetherParser(["alwaysplay"], namespace = NAMESPACE)
    fun alwaysplay() = combinationParser {
        it.group(
            text(),
            command("scale", then = double()).option(),
            command("rotateX", then = double()).option(),
            command("rotateY", then = double()).option(),
            command("rotateZ", then = double()).option(),
            command("origin", then = action()).option(),
            command("they", then = action()).option(),
            command("onHit", then = text()).option(),
            command("onTick", then = text()).option()
        ).apply(it) { keys, scale, rotateX, rotateY, rotateZ, origin, selector, onHit, onTick ->
            now {
                showEffect(Type.ALWAYS_PLAY, keys, scale, rotateX, rotateY, rotateZ, origin, selector, onHit, onTick)
            }
        }

    }

    private fun ScriptFrame.showEffect(type: Type, keys: String, scale: Double?, rotateX: Double?, rotateY: Double?, rotateZ: Double?, origin: ParsedAction<*>?, selector: ParsedAction<*>?, onHit: String?, onTick: String?) {

        val viewer = containerOrSender(selector).get()

        val newOrigin = containerOrSender(origin).get()

        val group = EffectGroup()

        keys.split(",").forEach {

            val effectOption = variables().get<EffectOption>("effect_$it").get()

            when (effectOption.type) {
                EffectWing.name -> {

                    val spawner = EffectSpawner(effectOption, viewer) {
                        onTick?.let { it1 -> session().handleIncident(it1, IncidentEffectTick(it)) }
                        onHit?.let { it1 -> session().handleIncident(it1, IncidentEffectHit(it.entityAt())) }
                    }

                    newOrigin.forEachPlayer {
                        EffectWing.render(effectOption, spawner, this)
                    }

                    return@forEach
                }

                "point" -> {

                    val spawner = EffectSpawner(effectOption, viewer) {
                        onTick?.let { it1 -> session().handleIncident(it1, IncidentEffectTick(it)) }
                        onHit?.let { it1 -> session().handleIncident(it1, IncidentEffectHit(it.entityAt())) }
                    }

                    newOrigin.forEach {
                        it.getLocation()?.let { it1 -> spawner.spawn(it1) }
                    }

                    return@forEach
                }

                else -> {
                    newOrigin.forEach last@{

                        val effectObj = Effects.get(effectOption.type).getEffectObj(effectOption, getContext())

                        val location = it.getLocation() ?: return@last

                        when (effectObj) {

                            is NRankBezierCurve -> {}

                            is Ray -> {
                                effectObj.direction = adaptLocation(location).direction
                                effectObj.origin = adaptLocation(location)
                            }

                            else -> {
                                effectObj.origin = adaptLocation(location)
                            }

                        }

                        effectObj.spawner = EffectSpawner(effectOption, viewer) {
                            onTick?.let { it1 -> session().handleIncident(it1, IncidentEffectTick(it)) }
                            onHit?.let { it1 -> session().handleIncident(it1, IncidentEffectHit(it.entityAt())) }
                        }

                        group.addEffect(effectObj)

                    }
                }

            }

        }

        rotateX?.let { group.rotateAroundXAxis(it) }
        rotateY?.let { group.rotateAroundYAxis(it) }
        rotateZ?.let { group.rotateAroundZAxis(it) }

        scale?.let { group.scale(it) }

        when(type) {
            Type.ALWAYS_PLAY -> {
                group.alwaysPlayAsync()
            }
            Type.ALWAYS_SHOW -> {
                group.alwaysShowAsync()
            }
            Type.PLAY -> {
                group.play()
            }
            Type.SHOW -> {
                group.show()
            }
        }

    }

    enum class Type {
        PLAY,SHOW,ALWAYS_PLAY,ALWAYS_SHOW
    }


}
