package com.bh.planners.core.effect

import com.bh.planners.core.effect.util.get
import com.google.common.base.Enums
import taboolib.common.platform.ProxyParticle
import taboolib.common.util.Vector
import taboolib.common5.Coerce
import java.awt.Color

class EffectOption(var type: String) {

    val options = mutableMapOf<String, Option>()

    class Option(val line: String) {
        val args = line.split(" ")
    }

    //particle
    val particle: ProxyParticle
        get() = Enums.getIfPresent(ProxyParticle::class.java, options.get(arrayOf("particle", "p"), "flame")!!.line.uppercase()).or(ProxyParticle.FLAME)!!

    //offset
    val optOffsetVector
        get() = options.get(arrayOf("offset", "os"), "0 0 0")!!.args.map { it.toDouble() }

    val offsetX
        get() = Coerce.toDouble(optOffsetVector.getOrElse(0) { "0" })
    val offsetY
        get() = Coerce.toDouble(optOffsetVector.getOrElse(1) { "0" })
    val offsetZ
        get() = Coerce.toDouble(optOffsetVector.getOrElse(2) { "0" })

    val offsetVector
        get() = Vector(offsetX, offsetY, offsetZ)

    //origin
    val origin
        get() = options.get(arrayOf("origin", "o"), "@origin")!!.line

    //pos
    val pos
        get() = options.get("pos", "0 0 0")!!

    val posX
        get() = pos.args[0].toDouble()
    val posY
        get() = pos.args[1].toDouble()
    val posZ
        get() = pos.args[2].toDouble()

    //speed
    val speed
        get() = options.get(arrayOf("speed", "s"), "0")!!.line.toDouble()

    //count
    val count
        get() = options.get(arrayOf("count", "amount", "a"), "1")!!.line.toInt()

    //startAngle
    val startAngle
        get() = options.get(arrayOf("startAngle", "start"), "0")!!.line.toDouble()

    //endAngle
    val end
        get() = options.get(arrayOf("endAngle", "end"), "360")!!.line.toDouble()

    //radius
    val radius
        get() = options.get(arrayOf("radius", "r"), "3")!!.line.toDouble()

    //step
    val step
        get() = options.get(arrayOf("step"), "1")!!.line.toDouble()

    //period
    val period
        get() = options.get(arrayOf("period"), "1")!!.line.toLong()

    //mark
    val markA
        get() = options.get(arrayOf("markA"), "@origin")!!.line
    val markB
        get() = options.get(arrayOf("markB"), "@origin")!!.line

    //length
    val length
        get() = options.get(arrayOf("length"), "10")!!.line.toDouble()

    //range
    val range
        get() = options.get(arrayOf("range"), "3")!!.line.toDouble()

    //scaleX
    val scaleX
        get() = options.get(arrayOf("scaleX"), "2")!!.line.toDouble()

    //scaleY
    val scaleY
        get() = options.get(arrayOf("scaleY"), "2")!!.line.toDouble()

    //sample
    val sample
        get() = options.get(arrayOf("sample"), "20")!!.line.toInt()

    //sides
    val sides
        get() = options.get(arrayOf("sides"), "3")!!.line.toInt()

    //height
    val height
        get() = options.get(arrayOf("height"), "7")!!.line.toDouble()

    //width
    val width
        get() = options.get(arrayOf("width"), "5")!!.line.toDouble()

    //data
    val data: ProxyParticle.Data?
        get() = color ?: item ?: block


    private val block
        get() = options.get(arrayOf("block", "blockdata"))?.let {
            ProxyParticle.BlockData(
                it.args.getOrElse(0) { "DIRT" }.uppercase(),
                Coerce.toInteger(it.args.getOrElse(1) { "0" })
            )
        }
    private val item
        get() = options.get(arrayOf("item", "itemdata"))?.let {
            ProxyParticle.ItemData(
                it.args.getOrElse(0) { "block" }.uppercase(),
                Coerce.toInteger(it.args.getOrElse(1) { "1" }),
                it.args.getOrElse(2) { "" }
            )
        }

    private val color
        get() = options.get(arrayOf("color", "colordata"))?.let {
            ProxyParticle.DustData(
                Color(
                    Coerce.toInteger(it.args.getOrElse(0) { "0" }),
                    Coerce.toInteger(it.args.getOrElse(1) { "1" }),
                    Coerce.toInteger(it.args.getOrElse(2) { "2" })
                ),
                Coerce.toFloat(it.args.getOrElse(3) { "1" })
            )
        }

    override fun toString(): String {
        return options.toString()
    }

}
