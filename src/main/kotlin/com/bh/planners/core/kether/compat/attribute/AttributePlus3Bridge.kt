package com.bh.planners.core.kether.compat.attribute

import com.bh.planners.api.common.SimpleUniqueTask
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.serverct.ersha.api.AttributeAPI
import taboolib.common.OpenResult
import taboolib.common.util.random
import taboolib.common5.cdouble
import taboolib.module.kether.KetherLoader
import taboolib.module.kether.ScriptProperty
import taboolib.module.kether.isInt
import java.util.*

class AttributePlus3Bridge : AttributeBridge {


    init {
        KetherLoader.registerProperty(object : ScriptProperty<Array<Any?>>("array.operator") {

            override fun read(instance: Array<Any?>, key: String): OpenResult {
                return when {
                    key == "random" -> {
                        if (instance.size == 2) {
                            OpenResult.successful(random(instance[0].cdouble, instance[1].cdouble))
                        } else {
                            OpenResult.successful(random(0.0, instance[0].cdouble))
                        }
                    }

                    key.isInt() -> OpenResult.successful(instance[key.toInt()])
                    key == "length" || key == "size" -> OpenResult.successful(instance.size)
                    else -> OpenResult.failed()
                }
            }

            override fun write(instance: Array<Any?>, key: String, value: Any?): OpenResult {
                return if (key.isInt()) {
                    instance[key.toInt()] = value
                    OpenResult.successful()
                } else {
                    OpenResult.failed()
                }
            }
        }, bind = Array::class.java, shared = false)
    }

    val LivingEntity.getAttributeData
        get() = AttributeAPI.getAttrData(this)

    override fun addAttributes(uuid: UUID, timeout: Long, reads: List<String>) {
        addAttributes(UUID.randomUUID().toString(), uuid, timeout, reads)
    }

    override fun addAttributes(source: String, uuid: UUID, timeout: Long, reads: List<String>) {
        val entity = Bukkit.getEntity(uuid) as? LivingEntity ?: return
        val attributeSource = AttributeAPI.getAttributeSource(reads)

        AttributeAPI.addSourceAttribute(entity.getAttributeData, source, attributeSource)
        if (timeout != -1L) {
            SimpleUniqueTask.submit("$uuid:$source", timeout / 50) {
                removeAttributes(uuid, source)
            }
        }
    }

    override fun removeAttributes(uuid: UUID, source: String) {
        SimpleUniqueTask.remove("$uuid:$source")
        val entity = Bukkit.getEntity(uuid) as? LivingEntity ?: error("null")
        AttributeAPI.takeSourceAttribute(entity.getAttributeData, source)
    }

    override fun update(entity: LivingEntity) {

    }

    override fun update(uuid: UUID) {

    }

    override fun get(uuid: UUID, keyword: String): Any {
        return 0
    }

    override fun get(entity: LivingEntity, keyword: String): Any {
        val data = entity.getAttributeData
        return data.getAttributeValue(keyword)
    }

}