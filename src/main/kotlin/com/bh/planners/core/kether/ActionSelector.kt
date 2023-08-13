package com.bh.planners.core.kether

import com.bh.planners.core.effect.Target
import com.bh.planners.core.kether.common.CombinationKetherParser
import com.bh.planners.core.kether.common.KetherHelper.containerOrEmpty
import com.bh.planners.core.kether.common.KetherHelper.simpleKetherParser
import com.bh.planners.core.kether.common.ParameterKetherParser
import taboolib.common.OpenResult
import taboolib.module.kether.KetherProperty
import taboolib.module.kether.ScriptProperty


/**
 * selector
 *
 *
 */
@CombinationKetherParser.Used
object ActionSelector : ParameterKetherParser("selector") {

    private val EMPTY_CONTAINER = Target.Container()

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

    // selector <id> list
    val list = argumentKetherNow { id ->
        variables().get<Target.Container>(id.toString()).orElseGet { EMPTY_CONTAINER }
    }

    val other = list

    // selector <id> remove
    val remove = argumentKetherNow { id ->
        variables().remove(id!!.toString())
    }

    // selector <id> unmerge <selector>
    val unmerge = simpleKetherParser<Unit>("unmerge") {
        it.group(containerOrEmpty()).apply(it) { value ->
            argumentNow { id ->
                variables().get<Target.Container>(id.toString()).ifPresent {
                    it.unmerge(value)
                }
            }
        }
    }

    // selector <id> merge <selector>
    val merge = simpleKetherParser<Unit>("merge") {
        it.group(containerOrEmpty()).apply(it) { value ->
            argumentNow { id ->
                variables().get<Target.Container>(id.toString()).ifPresent {
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