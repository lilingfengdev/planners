package com.bh.planners.core.kether.common

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
            val parser = combinationKetherParser.run()
            val id = combinationKetherParser.id
            val namespace = combinationKetherParser.namespace
            KetherLoader.registerParser(parser, id, namespace, true)
        }
    }

    override fun visitEnd(clazz: Class<*>, instance: Supplier<*>?) {
        if (clazz.isAnnotationPresent(CombinationKetherParser.Used::class.java) && CombinationKetherParser::class.java.isAssignableFrom(clazz)) {
            if (clazz.isAnnotationPresent(CombinationKetherParser.Ignore::class.java)) {
                return
            }
            
            val combinationKetherParser = instance?.get() as? CombinationKetherParser ?: return
            val parser = combinationKetherParser.run()
            val id = combinationKetherParser.id
            val namespace = combinationKetherParser.namespace
            // 如果渲染的周期是具有状态特性的 则执行on init
            if (combinationKetherParser is Stateable) {
                combinationKetherParser.onInit()
            }
            KetherLoader.registerParser(parser, id, namespace, true)
        }
    }

}

