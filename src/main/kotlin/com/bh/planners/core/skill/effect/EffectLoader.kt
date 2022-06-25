package com.bh.planners.core.skill.effect

import com.bh.planners.core.kether.event.ActionEventParser
import taboolib.common.LifeCycle
import taboolib.common.inject.Injector
import taboolib.common.platform.Awake
import taboolib.module.kether.KetherError
import taboolib.module.kether.KetherParser
import java.lang.reflect.Method
import java.util.function.Supplier


@Suppress("UNCHECKED_CAST")
@Awake
object EffectLoader : Injector.Methods {

    override val priority: Byte
        get() = 0

    override val lifeCycle: LifeCycle
        get() = LifeCycle.LOAD

    override fun inject(method: Method, clazz: Class<*>, instance: Supplier<*>) {
        if (method.isAnnotationPresent(KetherParser::class.java) && method.returnType == EffectParser::class.java) {
            val annotation = method.getAnnotation(KetherParser::class.java)
            val parser = method.invoke(instance.get()) as EffectParser
            Effects.parsers[annotation.value] = parser
        }

    }


}