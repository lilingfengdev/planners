package com.bh.planners.core.kether.compat.attribute

import ac.github.oa.api.OriginAttributeAPI
import ac.github.oa.internal.core.attribute.AbstractAttribute
import ac.github.oa.internal.core.attribute.Attribute
import ac.github.oa.internal.core.attribute.AttributeData
import ac.github.oa.internal.core.attribute.AttributeManager
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import taboolib.common.OpenResult
import taboolib.common5.cint
import taboolib.module.kether.KetherLoader
import taboolib.module.kether.ScriptProperty
import taboolib.module.kether.isInt
import java.util.*
import kotlin.math.max

class OriginAttributeBridge : AttributeBridge {

    val cache = mutableMapOf<String, Int>()

    init {
        KetherLoader.registerProperty(object : ScriptProperty<AttributeData.Data>("attributedata.operator") {
            override fun read(instance: AttributeData.Data, key: String): OpenResult {
                return when {
                    key == "random" -> OpenResult.successful(instance.random())
                    key.isInt() -> OpenResult.successful(instance.get(key.cint))
                    else -> OpenResult.failed()
                }
            }

            override fun write(instance: AttributeData.Data, key: String, value: Any?): OpenResult {
                return OpenResult.failed()
            }

        }, AttributeData.Data::class.java, false)
    }

    override fun addAttributes(uuid: UUID, timeout: Long, reads: List<String>) {
        addAttributes(UUID.randomUUID().toString(), uuid, timeout, reads)
    }

    override fun addAttributes(source: String, uuid: UUID, timeout: Long, reads: List<String>) {
        val data = Data(timeout)
        data.merge(OriginAttributeAPI.loadList(reads))
        OriginAttributeAPI.setExtra(uuid, source, data)
    }

    override fun update(entity: LivingEntity) {
        OriginAttributeAPI.callUpdate(entity)
    }

    override fun update(uuid: UUID) {
        this.update(Bukkit.getEntity(uuid) as? LivingEntity ?: return)
    }

    override fun removeAttributes(uuid: UUID, source: String) {
        OriginAttributeAPI.removeExtra(uuid, source)
    }

    override fun get(uuid: UUID, keyword: String): Any {
        return get(Bukkit.getEntity(uuid) as LivingEntity, keyword)
    }

    fun getEntities(attribute: Attribute): MutableList<Attribute.Entry> {
        return (attribute as AbstractAttribute).entries
    }

    fun Attribute.searchByKeyword(keyword: String): Attribute.Entry {
        return getEntities(this).firstOrNull { keyword in it.keywords } ?: error("Attribute [$keyword] not found.")
    }

    override fun get(entity: LivingEntity, keyword: String): Any {
        val data = OriginAttributeAPI.getAttributeData(entity)
        val index = cache.computeIfAbsent(keyword) {
            AttributeManager.usableAttributes.values.firstOrNull {
                keyword in getEntities(it).flatMap { it.keywords }
            }?.getPriority() ?: -1
        }
        if (index == -1) error("Attribute [$keyword] not found.")
        val entry = AttributeManager.getAttribute(index).searchByKeyword(keyword)
        val arrayData = data.getArrayData(index)
        return arrayData[entry.index]
    }

    class Data(val timeout: Long) : AttributeData() {

        val create = System.currentTimeMillis()

        val end: Long
            get() = timeout + create

        val countdown: Long
            get() = max(end - System.currentTimeMillis(), 0)

        override val isValid: Boolean
            get() = timeout == -1L || countdown > 0L

    }

}