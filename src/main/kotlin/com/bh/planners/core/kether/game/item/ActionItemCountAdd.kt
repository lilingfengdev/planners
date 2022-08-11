package com.bh.planners.core.kether.game.item

import com.bh.planners.core.kether.asPlayer
import com.bh.planners.core.kether.execLivingEntity
import com.bh.planners.core.kether.game.item.ItemOperator.getNumber
import com.bh.planners.core.kether.runTransfer
import org.bukkit.inventory.ItemStack
import taboolib.common5.Coerce
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.platform.util.isAir
import taboolib.platform.util.modifyLore
import taboolib.type.BukkitEquipment
import java.util.concurrent.CompletableFuture

class ActionItemCountAdd (
    val slot: ParsedAction<*>,
    val keyword: ParsedAction<*>,
    val amount: ParsedAction<*>,
    val max: ParsedAction<*>,
    val selector: ParsedAction<*>?
) : ScriptAction<Void>() {



    override fun run(frame: ScriptFrame): CompletableFuture<Void> {

        frame.runTransfer<BukkitEquipment>(slot).thenAccept { slot ->
            frame.runTransfer<String>(keyword).thenAccept { keyword ->
                frame.runTransfer<Int>(amount).thenAccept { amount ->
                    frame.runTransfer<Int>(max).thenAccept { max ->
                        if (selector != null) {
                            frame.execLivingEntity(selector) {
                                execute(slot.getItem(this), keyword, amount, max)
                            }
                        } else {
                            execute(slot.getItem(frame.asPlayer()), keyword, amount, max)
                        }
                    }
                }
            }
        }

        return CompletableFuture.completedFuture(null)
    }

    fun execute(item: ItemStack?, keyword: String, amount: Int, max: Int) {
        if (item == null || item.isAir() || item.itemMeta?.hasLore() == false) return
        item.modifyLore {
            this.forEachIndexed { index, s ->
                if (s.contains(keyword)) {
                    val number = getNumber(s)
                    if (Coerce.toInteger(number) >= max) return@forEachIndexed
                    this[index] = s.replace(number, (Coerce.toInteger(number) + amount).toString())
                }
            }
        }
    }

}