package com.bh.planners.core.kether

import com.bh.planners.core.effect.Target
import com.bh.planners.core.kether.common.*
import taboolib.common.OpenResult
import taboolib.common.platform.function.warning
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

@CombinationKetherParser.Used
object ActionSelector : ParameterKetherParser("selector") {

    // selector <id> set <selector(不需要they at 关键字)>
    // 错误的写法 selector a0 set they @self
    // 正确的写法 selector a0 set @self
    val set = simpleKetherParser<Unit>("to", "they", "at", "the") {
        it.group(any()).apply(it) { value ->
            argumentNow { id ->
                variables()[id.toString()] = parseTargetContainer(value!!, getContext())
            }
        }
    }

    // selector <id> remove
    val remove = argumentKetherNow { id ->
        variables().remove(id!!.toString())
    }

    // selector <id> unmerge <selector>
    val unmerge = simpleKetherParser<Unit>("unmerge") {
        it.group(containerOrEmpty()).apply(it) { value ->
            argumentNow { id ->
                variables().get<Target.Container>(id!!.toString()).ifPresent {
                    it.unmerge(value)
                }
            }
        }
    }

    // selector <id> merge <selector>
    val merge = simpleKetherParser<Unit>("merge") {
        it.group(containerOrEmpty()).apply(it) { value ->
            argumentNow { id ->
                variables().get<Target.Container>(id!!.toString()).ifPresent {
                    it.merge(value)
                }
            }
        }
    }

    @KetherProperty(bind = Target.Container::class)
    fun propertyArray() = object : ScriptProperty<Target.Container>("target.container.operator") {

        override fun read(instance: Target.Container, key: String): OpenResult {
            return when (key) {
                "length", "size" -> OpenResult.successful(instance.size)
                else -> OpenResult.failed()
            }
        }

        override fun write(instance: Target.Container, key: String, value: Any?): OpenResult {
            return OpenResult.failed()
        }
    }
}

@CombinationKetherParser.Used
fun select() = simpleKetherParser<Target.Container>("select") {
    it.group(any()).apply(it) { v ->
        now { parseTargetContainer(v!!,getContext()) }
    }
}