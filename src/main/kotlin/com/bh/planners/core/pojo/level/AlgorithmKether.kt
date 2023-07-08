package com.bh.planners.core.pojo.level

import taboolib.common5.Coerce
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.kether.KetherShell.eval
import taboolib.module.kether.ScriptOptions
import taboolib.module.kether.printKetherErrorMessage
import java.util.concurrent.CompletableFuture

class AlgorithmKether(val section: ConfigurationSection) : Algorithm() {


    override val minLevel: Int
        get() = section.getInt("min")

    override val maxLevel: Int
        get() = section.getInt("max")

    override fun getExp(level: Int): CompletableFuture<Int> {
        return try {
            eval(section.getString("experience").toString(), ScriptOptions.builder().namespace(emptyList()).context {
                rootFrame().variables().set("level", level)
            }.build()).thenApply {
                Coerce.toInteger(it)
            }
        } catch (ex: Exception) {
            ex.printKetherErrorMessage()
            CompletableFuture.completedFuture(0)
        }
    }
}
