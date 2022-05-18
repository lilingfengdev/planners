package com.bh.planners.core.pojo.level

import taboolib.common5.Coerce
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.kether.KetherShell
import taboolib.module.kether.printKetherErrorMessage
import java.util.concurrent.CompletableFuture

class AlgorithmKether(val section: ConfigurationSection) : Algorithm() {

    override val maxLevel: Int
        get() = section.getInt("max")

    override fun getExp(level: Int): CompletableFuture<Int> {
        return try {
            KetherShell.eval(section.getString("experience").toString()) {
                rootFrame().variables().set("level", level)
            }.thenApply {
                Coerce.toInteger(it)
            }
        } catch (ex: Exception) {
            ex.printKetherErrorMessage()
            CompletableFuture.completedFuture(0)
        }
    }
}
