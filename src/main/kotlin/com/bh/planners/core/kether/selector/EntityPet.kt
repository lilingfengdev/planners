package com.bh.planners.core.kether.selector

import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.pojo.Context
import de.Keyle.MyPet.MyPetApi
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

class EntityPet : Selector {

    override val names: Array<String>
        get() = arrayOf("pet", "e-pet", "!pet", "!e-pet")

    override fun check(name: String, target: Target?, args: String, context: Context, container: Target.Container): CompletableFuture<Void> {
        val entityTarget = target as? Target.Entity ?: return CompletableFuture.completedFuture(null)

        val player = (entityTarget.entity as? Player) ?: return CompletableFuture.completedFuture(null)


        val myPet = MyPetApi.getMyPetManager().getMyPet(player) ?: return CompletableFuture.completedFuture(null)
        myPet.entity.ifPresent { entity ->
            if (name.isNon()) {
                container.removeIf { entity == it }
            } else {
                container += entity.toTarget()
            }
        }


        return CompletableFuture.completedFuture(null)

    }


}