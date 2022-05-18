package com.bh.planners.core.storage

import com.bh.planners.core.pojo.player.PlayerProfile
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

interface Storage {

    companion object {

        val INSTANCE by lazy {
            StorageSQL()
        }

        fun Player.toUserId(): Long {
            return INSTANCE.getUserId(this).get()
        }


    }

    fun loadProfile(player: Player): CompletableFuture<PlayerProfile>

    fun getUserId(player: Player): CompletableFuture<Long>
}
