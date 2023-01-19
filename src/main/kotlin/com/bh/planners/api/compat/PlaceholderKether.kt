package com.bh.planners.api.compat

import com.bh.planners.api.ContextAPI
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.kether.namespaces
import com.bh.planners.core.pojo.Context
import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.kether.KetherFunction
import taboolib.module.kether.KetherShell
import taboolib.module.kether.printKetherErrorMessage
import taboolib.platform.compat.PlaceholderExpansion

object PlaceholderKether : PlaceholderExpansion {

    private val cache = mutableMapOf<Player, Context.Impl0>()

    private fun getContext(player: Player) = cache.computeIfAbsent(player) {
        Context.Impl0(player.toTarget())
    }

    override val identifier: String
        get() = "planners"

    override fun onPlaceholderRequest(player: Player?, args: String): String {
        return try {
            KetherShell.eval(args, sender = adaptPlayer(player!!), namespace = namespaces) {
                rootFrame().variables()["@Context"] = getContext(player)
            }.get().toString()
        } catch (e: Throwable) {
            e.printKetherErrorMessage()
            "none-error"
        }
    }
}
