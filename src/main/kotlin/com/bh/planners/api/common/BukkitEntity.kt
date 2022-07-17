package com.bh.planners.api.common

import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import taboolib.common.platform.ProxyCommandSender

class BukkitEntity(val instance: Entity) : ProxyCommandSender {
    override var isOp: Boolean
        get() = false
        set(value) {}
    override val name: String
        get() = instance.name
    override val origin: Any
        get() = TODO("Not yet implemented")

    override fun hasPermission(permission: String): Boolean {
        return false
    }

    override fun isOnline(): Boolean {
        return instance.isDead
    }

    override fun performCommand(command: String): Boolean {
        return false
    }

    override fun sendMessage(message: String) {
        if (instance is LivingEntity) instance.sendMessage(message)
    }
}