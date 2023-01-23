package com.bh.planners.core.kether.game.item

import com.bh.planners.core.kether.bukkitPlayer
import com.bh.planners.core.kether.createContainer
import com.bh.planners.core.kether.execLivingEntity
import com.bh.planners.core.kether.game.item.ItemOperator.getNumber
import com.bh.planners.core.kether.read
import org.bukkit.inventory.ItemStack
import taboolib.common5.Coerce
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.platform.util.isAir
import taboolib.platform.util.modifyLore
import taboolib.type.BukkitEquipment
import java.util.concurrent.CompletableFuture

class ActionItemCountGet(
    val slot: ParsedAction<*>,
    val keyword: ParsedAction<*>,
    val selector: ParsedAction<*>?
) : ScriptAction<Int>() {


    override fun run(frame: ScriptFrame): CompletableFuture<Int> {
        val future = CompletableFuture<Int>()
        frame.read<BukkitEquipment>(slot).thenAccept { slot ->
            frame.read<String>(keyword).thenAccept { keyword ->
                if (selector != null) {
                    frame.createContainer(selector).thenAccept {
                        val entityTarget = it.firstLivingEntityTarget()
                        if (entityTarget != null) {
                            future.complete(get(slot.getItem(entityTarget), keyword))
                        } else {
                            future.complete(null)
                        }
                    }
                } else {
                    future.complete(get(slot.getItem(frame.bukkitPlayer()), keyword))
                }
            }
        }

        return future
    }

    fun get(item: ItemStack?, keyword: String): Int {
        if (item == null || item.isAir() || item.itemMeta?.hasLore() == false) return -1
        var amount = -1
        item.modifyLore {
            this.forEachIndexed { index, s ->
                if (s.contains(keyword)) {
                    amount = Coerce.toInteger(getNumber(s))
                }
            }
        }
        return amount
    }



}