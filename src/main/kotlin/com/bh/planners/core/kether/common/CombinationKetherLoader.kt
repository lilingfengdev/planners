package com.bh.planners.core.kether.common

import com.bh.planners.api.common.Plugin
import org.bukkit.Bukkit
import taboolib.common.LifeCycle
import taboolib.common.inject.ClassVisitor
import taboolib.common.platform.Awake
import taboolib.common.platform.function.info
import taboolib.library.reflex.ClassMethod
import taboolib.module.kether.KetherLoader
import taboolib.module.kether.ScriptActionParser
import java.util.function.Supplier


@Awake
class Visitor : ClassVisitor(0) {

    override fun getLifeCycle(): LifeCycle {
        return LifeCycle.LOAD
    }

    override fun visit(method: ClassMethod, clazz: Class<*>, instance: Supplier<*>?) {
        if (method.isAnnotationPresent(CombinationKetherParser.Used::class.java) && CombinationKetherParser::class.java.isAssignableFrom(method.returnType)) {
            if (method.isAnnotationPresent(CombinationKetherParser.Ignore::class.java)) {
                return
            }
            val combinationKetherParser = (if (instance == null) method.invokeStatic() else method.invoke(instance.get())) as CombinationKetherParser
            KetherHelper.registerCombinationKetherParser(method.name, combinationKetherParser)
        }
    }

    override fun visitEnd(clazz: Class<*>, instance: Supplier<*>?) {
        if (clazz.isAnnotationPresent(CombinationKetherParser.Used::class.java) && CombinationKetherParser::class.java.isAssignableFrom(clazz)) {
            if (clazz.isAnnotationPresent(CombinationKetherParser.Ignore::class.java)) {
                return
            }

            // 检查前置
            if (clazz.isAnnotationPresent(Plugin::class.java) && Bukkit.getPluginManager().getPlugin(clazz.getAnnotation(Plugin::class.java).name) == null) {
                return
            }

            val combinationKetherParser = instance?.get() as? CombinationKetherParser ?: return
            KetherHelper.registerCombinationKetherParser(combinationKetherParser)
        }
    }

}

