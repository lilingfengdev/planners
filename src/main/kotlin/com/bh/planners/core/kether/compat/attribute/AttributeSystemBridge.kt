//package com.bh.planners.core.kether.compat.attribute
//
//import com.bh.planners.api.common.SimpleUniqueTask
//import com.skillw.attsystem.api.AttrAPI.addCompiledData
//import com.skillw.attsystem.api.AttrAPI.read
//import com.skillw.attsystem.api.AttrAPI.removeCompiledData
//import com.skillw.attsystem.api.AttrAPI.update
//import com.skillw.pouvoir.util.livingEntity
//import org.bukkit.Bukkit
//import org.bukkit.entity.LivingEntity
//import java.util.*
//import kotlin.math.max
//
//class AttributeSystemBridge : AttributeBridge {
//
//    override fun addAttributes(uuid: UUID, timeout: Long, reads: List<String>) {
//        addAttributes(UUID.randomUUID().toString(), uuid, timeout, reads)
//    }
//
//    override fun addAttributes(source: String, uuid: UUID, timeout: Long, reads: List<String>) {
//        val entity = Bukkit.getEntity(uuid) as? LivingEntity ?: error("null")
//        val compiledData = reads.read(entity) ?: return
//        uuid.addCompiledData(source, compiledData)
//        if (timeout != -1L) {
//            SimpleUniqueTask.submit("$uuid:$source", timeout / 50) {
//                removeAttributes(uuid, source)
//            }
//        }
//    }
//
//    override fun removeAttributes(uuid: UUID, source: String) {
//        SimpleUniqueTask.remove("$uuid:$source")
//        uuid.removeCompiledData(source)
//    }
//
//    override fun update(entity: LivingEntity) {
//        entity.update()
//    }
//
//    override fun update(uuid: UUID) {
//        uuid.livingEntity()?.let { update(it) }
//    }
//
//    override fun get(uuid: UUID, keyword: String): Any {
//        return 0
//    }
//
//    class Data(val source: String, val timeout: Long) {
//
//        val create = System.currentTimeMillis()
//
//        val end: Long
//            get() = timeout + create
//
//        val countdown: Long
//            get() = max(end - System.currentTimeMillis(), 0)
//
//        val isValid: Boolean
//            get() = countdown > 0L
//
//    }
//}