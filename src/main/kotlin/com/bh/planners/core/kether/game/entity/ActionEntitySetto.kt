package com.bh.planners.core.kether.game.entity

import com.bh.planners.api.entity.ProxyEntity
import com.bh.planners.core.kether.createContainer
import com.bh.planners.core.kether.origin
import org.bukkit.Location
import org.bukkit.entity.Entity
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import java.util.concurrent.CompletableFuture
import kotlin.math.atan

class ActionEntitySetto(
    val selector: ParsedAction<*>,
    val faceto: ParsedAction<*>?,
) : ScriptAction<List<Entity>>() {

    fun faceto(face: ProxyEntity, faceto: Location) {
        val locA = face.location.clone()
        val distance = face.location.distance(faceto)
        val sub = locA.subtract(faceto)
        val pitch = Math.toDegrees(atan(sub.y/distance))
        val yaw = Math.toDegrees(atan(sub.x/sub.z))

        face.location.yaw = yaw.toFloat()
        face.location.pitch = pitch.toFloat()
    }

    override fun run(frame: ScriptFrame): CompletableFuture<List<Entity>> {
        frame.createContainer(selector).thenAccept { container ->
            if (faceto != null) {
                frame.createContainer(faceto).thenAccept { target ->
                    container.forEachProxyEntity {
                        target.firstProxyEntity()?.let { it1 -> faceto(this, it1.location) }
                    }
                }
            } else {
                container.forEachProxyEntity {
                    faceto(this, frame.origin().value)
                }
            }
        }
        return CompletableFuture.completedFuture(null)
    }

}