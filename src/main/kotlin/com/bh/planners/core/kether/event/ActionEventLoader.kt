package com.bh.planners.core.kether.event

import taboolib.common.LifeCycle
import taboolib.common.inject.Injector
import taboolib.common.platform.Awake
import taboolib.common.platform.function.info
import taboolib.library.kether.LocalizedException
import taboolib.library.kether.QuestAction
import taboolib.library.kether.QuestReader
import taboolib.module.kether.KetherError
import taboolib.module.kether.KetherParser
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptActionParser
import java.lang.reflect.Method
import java.util.Arrays
import java.util.function.Supplier

@Suppress("UNCHECKED_CAST")
@Awake
object ActionEventLoader : Injector.Methods {

    val actions = mutableMapOf<Array<String>, ActionEventParser>()

    fun getAction(name: String): ActionEventParser {
        val entry = actions.entries.firstOrNull { name in it.key } ?: throw KetherError.NOT_SYMBOL.create(name)
        return entry.value
    }


    override val priority: Byte
        get() = 0

    override val lifeCycle: LifeCycle
        get() = LifeCycle.LOAD

    override fun inject(method: Method, clazz: Class<*>, instance: Supplier<*>) {
        if (method.isAnnotationPresent(KetherParser::class.java) && method.returnType == ActionEventParser::class.java) {
            val annotation = method.getAnnotation(KetherParser::class.java)
            val parser = method.invoke(instance.get()) as ActionEventParser
            actions[annotation.value] = parser
        }

    }


}
