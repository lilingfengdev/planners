package com.bh.planners.core.effect

import com.bh.planners.core.kether.event.ActionEventParser
import taboolib.common.LifeCycle
import taboolib.common.inject.ClassVisitor
import taboolib.common.platform.Awake
import taboolib.library.reflex.ClassMethod
import taboolib.module.kether.KetherError
import taboolib.module.kether.KetherParser
import java.lang.reflect.Method
import java.util.function.Supplier


@Awake
object EffectLoader : ClassVisitor(0) {

    override fun getLifeCycle(): LifeCycle {
        return LifeCycle.LOAD
    }

    override fun visit(method: ClassMethod, clazz: Class<*>, instance: Supplier<*>?) {

        if (method.isAnnotationPresent(KetherParser::class.java) && method.returnType == EffectParser::class.java) {
            val classInstance = instance?.get() ?: return
            val parser = method.invoke(classInstance) as EffectParser
            val annotation = method.getAnnotation(KetherParser::class.java)
            Effects.parsers[annotation.property("value", arrayOf())] = parser
        }
    }


}