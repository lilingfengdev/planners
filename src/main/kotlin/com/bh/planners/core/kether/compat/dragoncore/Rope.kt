package com.bh.planners.core.kether.compat.dragoncore

import eos.moe.dragoncore.api.CoreAPI
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Entity

class Rope(val entity1: Entity, val entity2: Entity, val path: String, val key: String) {

    fun rend() {
        p1(entity1.location, entity2.location)
    }

    fun p1(pos1: Location, pos2: Location) {
        val v1 = pos1.direction.clone()
        val v2 = pos2.direction.clone()
        val midpoint = v1.getMidpoint(v2).normalize().toLocation(pos1.world!!)
        Bukkit.getOnlinePlayers().forEach {
            CoreAPI.setPlayerWorldTexture(
                it.player,
                "${key}1",
                midpoint,
                20F,
                20F,
                20F,
                path,
                pos1.distance(pos2).toFloat(),
                1F,
                1F,
                false,
                true
            )
        }
    }


}