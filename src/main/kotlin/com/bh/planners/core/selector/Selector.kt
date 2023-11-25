package com.bh.planners.core.selector

import com.bh.planners.api.common.Demand
import com.bh.planners.api.common.Plugin
import com.bh.planners.core.effect.Target
import com.bh.planners.core.pojo.Context
import com.bh.planners.core.selector.bukkit.EntityId.isNon
import org.bukkit.Bukkit
import org.bukkit.Material
import taboolib.common.LifeCycle
import taboolib.common.io.getInstance
import taboolib.common.io.runningClasses
import taboolib.common.platform.Awake
import taboolib.common5.Coerce
import java.util.concurrent.CompletableFuture

interface Selector {

    companion object {

        val selectors = mutableListOf<Selector>()

        fun getSelector(string: String): Selector {
            return selectors.firstOrNull { string in it.names } ?: error("Selector '${string}' not found")
        }

        fun check(context: Context, demand: Demand): Target.Container {
            val container = Target.Container()
            check(context, demand, container).get()
            return container
        }

        fun check(context: Context, demand: Demand, container: Target.Container): CompletableFuture<Void> {
            return SelectorTransfer(context, demand.source, container).run()
        }

        @Awake(LifeCycle.LOAD)
        fun load() {
            runningClasses.forEach {
                if (Selector::class.java.isAssignableFrom(it)) {
                    if (it.isAssignableFrom(Plugin::class.java)) {
                        val annotation = it.getAnnotation(Plugin::class.java)
                        if (!Bukkit.getPluginManager().isPluginEnabled(annotation.name)) {
                            return@forEach
                        }
                    }
                    selectors += (it.getInstance()?.get() as? Selector) ?: return@forEach
                }
            }
        }

    }

    val names: Array<String>

    fun check(data: Data): CompletableFuture<Void>

    fun String.isNon(): Boolean {
        return get(0) == '!'
    }

    class Data(val name: String, val source: String, val context: Context, val container: Target.Container) {

        var origin: Target = context.origin

        private val args = source.split(" ")

        val values: List<String>
            get() = args

        val size: Int
            get() = container.size

        val isNon: Boolean
            get() = name.isNon()

        fun getOrDefault(index: Int, def: String): String {
            return args.getOrElse(index) { def }
        }

        fun getOrNull(index: Int): String? {
            return args.getOrNull(index)
        }

        inline fun <reified T> read(index: Int, def: String): T {
            val value = getOrDefault(index, def)
            return when (T::class) {
                String::class -> Coerce.toString(value)
                Int::class -> Coerce.toInteger(value)
                Long::class -> Coerce.toLong(value)
                Boolean::class -> Coerce.toBoolean(value)
                Double::class -> Coerce.toDouble(value)
                Float::class -> Coerce.toFloat(value)

                Material::class.java -> try {
                    Material.valueOf(value)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Material.valueOf("STONE")
                }

                else -> value

            } as T
        }

    }

}
