package com.bh.planners.core.kether.event

import com.bh.planners.core.effect.EffectParser
import com.bh.planners.core.effect.Effects
import taboolib.common.LifeCycle
import taboolib.common.inject.ClassVisitor
import taboolib.common.platform.Awake
import taboolib.common.platform.function.info
import taboolib.library.kether.LocalizedException
import taboolib.library.kether.QuestAction
import taboolib.library.kether.QuestReader
import taboolib.library.reflex.ClassMethod
import taboolib.module.kether.KetherError
import taboolib.module.kether.KetherParser
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptActionParser
import java.lang.reflect.Method
import java.util.Arrays
import java.util.function.Supplier

@Suppress("UNCHECKED_CAST")
@Awake
object ActionEventLoader : ClassVisitor(0) {

    val actions = mutableMapOf<Array<String>, ActionEventParser>()

    fun getAction(name: String): ActionEventParser {
        val entry = actions.entries.firstOrNull { name in it.key } ?: throw KetherError.NOT_SYMBOL.create(name)
        return entry.value
    }

    override fun getLifeCycle(): LifeCycle {
        return LifeCycle.LOAD
    }

    override fun visit(method: ClassMethod, clazz: Class<*>, instance: Supplier<*>?) {
        if (method.isAnnotationPresent(KetherParser::class.java) && method.returnType == ActionEventParser::class.java) {
            val classInstance = instance?.get() ?: return
            val parser = method.invoke(classInstance) as ActionEventParser
            val annotation = method.getAnnotation(KetherParser::class.java)
            actions[annotation.property<Array<String>>("value")!!] = parser
        }
    }



}
