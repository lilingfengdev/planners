package com.bh.planners.core.pojo.data

import org.bukkit.Location
import taboolib.module.nms.ItemTag
import taboolib.module.nms.ItemTagData
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Consumer
import java.util.function.Function

/**
 * Chemdah
 * com.bh.planners.core.pojo.data.DataContainer
 *
 * @author sky
 * @since 2021/3/2 12:00 上午
 */
class DataContainer {

    val map = ConcurrentHashMap<String, Data>()
    private var locked = false

    /**
     * 数据是否发生变动
     */
    val isChanged: Boolean
        get() = map.any { it.value.changed }

    fun unchanged(func: Consumer<DataContainer>) {
        locked = true
        func.accept(this)
        locked = false
    }

    internal fun unchanged(func: DataContainer.() -> Unit) {
        unchanged(Consumer { func(this) })
    }

    constructor()


    constructor(map: Map<String, Data>) {
        this.map.putAll(map)
    }

    /**
     * 获取数据
     */
    operator fun get(key: String): Data? {
        val data = map[key] ?: return null
        return if (data.isOpened) data else null
    }

    /**
     * 获取数据并返回默认值
     */
    operator fun get(key: String, def: Any): Data {
        return this[key] ?: def.unsafeData()
    }

    /**
     * 修改数据
     */
    operator fun set(key: String, value: Data) {
        if (value.survivalStamp <= -1) {
            value.survivalStamp = -1
        }
        map[key] = value.change()
    }

    /**
     * 修改数据
     */
    fun update(key: String, value: Any) {
        if (containsKey(key)) {
            val data = get(key)!!
            set(key, Data(value, data.createStamp, data.survivalStamp))
        } else {
            set(key, Data(value, survivalStamp = -1))
        }
    }

    /**
     * 删除数据
     */
    fun remove(key: String) {
        map.remove(key)
    }

    /**
     * 清空数据
     */
    fun clear() {
        map.clear()
    }

    /**
     * 合并数据
     */
    fun merge(meta: DataContainer) {
        meta.forEach { (key, data) -> this[key] = data }
    }

    fun containsKey(key: String): Boolean {
        return map.filter { it.value.isOpened }.containsKey(key)
    }

    fun containsValue(value: Any): Boolean {
        return map.filter { it.value.isOpened }.containsValue(value.unsafeData())
    }

    fun entries(): Set<Map.Entry<String, Data>> {
        return map.filter { it.value.isOpened }.entries
    }

    fun keys(): List<String> {
        return map.filter { it.value.isOpened }.keys.toList()
    }

    fun copy(): DataContainer {
        return DataContainer(map)
    }

    /**
     * 释放变动
     */
    fun flush(): DataContainer {
        map.forEach {
            it.value.changed = false
        }
        return this
    }

    fun removeIf(predicate: Function<Map.Entry<String, Data>, Boolean>) {
        map.entries.forEach {
            if (predicate.apply(it)) {
                remove(it.key)
            }
        }
    }

    fun forEach(consumer: Consumer<Map.Entry<String, Data>>) {
        map.forEach { consumer.accept(it) }
    }

    fun toMap(): Map<String, Any> {
        return map.mapValues { it.value.data }
    }

    fun toNBT(): ItemTag {
        return ItemTag().also {
            map.forEach { (k, v) ->
                it[k] = ItemTag().also {
                    it["value"] = toNBT(v.data)
                    it["create-stamp"] = ItemTagData(v.createStamp)
                    it["survival-stamp"] = ItemTagData(v.survivalStamp)
                }
            }
        }
    }

    fun toNBT(any: Any): ItemTagData {

        return when (any) {
            is Location -> ItemTagData("${any.world!!.name},${any.x},${any.y},${any.z}")
            else -> ItemTagData.toNBT(any)
        }

    }

    fun toJson() = toNBT().toJson()

    fun isEmpty(): Boolean {
        return map.isEmpty()
    }

    fun isNotEmpty(): Boolean {
        return map.isNotEmpty()
    }

    private fun Data.change(): Data {
        if (!locked) {
            changed = true
        }
        return this
    }

    override fun toString(): String {
        return "DataCenter(map=$map)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DataContainer) return false
        if (map != other.map) return false
        return true
    }

    override fun hashCode(): Int {
        return map.hashCode()
    }

    companion object {

        fun Any.unsafeData(): Data {
            return Data.unsafeData(this, -1)
        }

        fun ItemTag.dataContainer(): DataContainer {
            val dataContainer = DataContainer()
            entries.forEach {
                val compound = it.value.asCompound()
                dataContainer[it.key] = Data(
                    compound["value"]!!.asString(),
                    compound["create-stamp"]!!.asLong(),
                    compound["survival-stamp"]!!.asLong(),
                )
            }
            return dataContainer
        }

        fun fromJson(source: String): DataContainer {
            return ItemTag.fromJson(source).dataContainer()
        }
    }
}
