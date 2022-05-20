package com.bh.planners.api.compat

import com.bh.planners.core.kether.namespaces
import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.kether.KetherFunction
import taboolib.module.kether.KetherShell
import taboolib.module.kether.printKetherErrorMessage
import taboolib.platform.compat.PlaceholderExpansion

object PlaceholderKether : PlaceholderExpansion {

    override val identifier: String
        get() = "planners"

    override fun onPlaceholderRequest(player: Player?, args: String): String {
        return try {
            KetherShell.eval(args, sender = adaptPlayer(player!!), namespace = namespaces).get().toString()
        } catch (e: Throwable) {
            e.printKetherErrorMessage()
            "none-error"
        }
    }
}
