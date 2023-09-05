package com.bh.planners.core.kether

import com.bh.planners.core.effect.Target
import com.bh.planners.core.kether.common.CombinationKetherParser
import com.bh.planners.core.kether.common.KetherHelper.simpleKetherParser
import com.bh.planners.core.kether.common.ParameterKetherParser
import taboolib.common.OpenResult
import taboolib.module.kether.*


/**
 * selector
 *
 *
 */
@CombinationKetherParser.Used
object ActionSelector : ParameterKetherParser("selector") {

    // selector <id> set <selector(不需要they at 关键字)>
    // 错误的写法 selector a0 to they @self
    // 正确的写法 selector a0 to @self
    val set = argumentKetherParser("to", "they", "at", "the") { argument ->
        val action = nextParsedAction()
        actionNow {
            run(argument).str { id ->
                run(action).thenAccept { value ->
                    variables()[id] = parseTargetContainer(value!!, getContext())
                }
            }
        }
    }

    // selector <id> list
    val list = argumentKetherParser { argument ->
        actionFuture {
            run(argument).str { id ->
                it.complete(variables().get<Target.Container>(id).orElseGet { Target.Container() })
            }
        }
    }

    val other = list

    // selector <id> remove
    val remove = argumentKetherNow { id ->
        variables().remove(id.toString())
    }

    // selector <id> unmerge they <selector>
    val unmerge = argumentKetherParser { argument ->
        val selector = nextSelectorOrNull()
        actionNow {
            run(argument).str { id ->
                containerOrEmpty(selector).thenAccept { selector ->
                    variables().get<Target.Container>(id).ifPresent {
                        it.unmerge(selector)
                    }
                }
            }
        }
    }

    // selector <id> merge they <selector>
    val merge = argumentKetherParser { argument ->
        val selector = nextSelectorOrNull()
        actionNow {
            run(argument).str { id ->
                containerOrEmpty(selector).thenAccept { selector ->
                    variables().get<Target.Container>(id).ifPresent {
                        it.merge(selector)
                    }
                }
            }
        }
    }

    // selector <id> size
    val size = argumentKetherParser { argument ->
        actionFuture {
            run(argument).str { id ->
                it.complete(variables().get<Target.Container>(id).orElseGet { Target.Container() }.size)
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
        now { parseTargetContainer(v!!, getContext()) }
    }
}