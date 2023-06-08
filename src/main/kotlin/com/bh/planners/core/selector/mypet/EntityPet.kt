package com.bh.planners.core.selector.mypet

import com.bh.planners.core.effect.Target.Companion.getPlayer
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.selector.Selector
import de.Keyle.MyPet.MyPetApi
import java.util.concurrent.CompletableFuture

object EntityPet : Selector {

    override val names: Array<String>
        get() = arrayOf("pet", "e-pet", "!pet", "!e-pet")

    override fun check(data: Selector.Data): CompletableFuture<Void> {
        val entityTarget = data.origin.getPlayer() ?: return CompletableFuture.completedFuture(null)

        val player = entityTarget.player ?: return CompletableFuture.completedFuture(null)

        val myPet = MyPetApi.getMyPetManager().getMyPet(player) ?: return CompletableFuture.completedFuture(null)
        myPet.entity.ifPresent { entity ->
            if (data.isNon) {
                data.container.removeIf { entity == it }
            } else {
                data.container += entity.toTarget()
            }
        }


        return CompletableFuture.completedFuture(null)

    }


}