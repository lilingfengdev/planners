package com.bh.planners.core.kether

import com.bh.planners.api.EntityAPI.deleteFlag
import com.bh.planners.api.EntityAPI.getDataContainer
import com.bh.planners.api.EntityAPI.getFlag
import com.bh.planners.api.EntityAPI.setFlag
import com.bh.planners.api.EntityAPI.updateFlag
import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.setFlag
import com.bh.planners.core.pojo.data.Data
import com.skillw.pouvoir.api.manager.sub.ContainerManager.Companion.getData
import taboolib.common5.Coerce
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionFlag {

    class DataGet(val action: ParsedAction<*>, val default: ParsedAction<*>, val selector: ParsedAction<*>?) : ScriptAction<Any>() {

        override fun run(frame: ScriptFrame): CompletableFuture<Any> {

            val future = CompletableFuture<Any>()
            frame.run(action).str { id ->
                frame.run(default).thenAccept { def ->
                    frame.containerOrSender(selector).thenApply {
                        future.complete(it.firstEntityTarget()?.getDataContainer()?.get(id)?.data ?: def)
                    }
                }
            }
            return future
        }

    }

    class DataSet(
        val action: ParsedAction<*>,
        val value: ParsedAction<*>,
        val time: ParsedAction<*>,
        val selector: ParsedAction<*>?
    ) : ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            frame.run(action).str { key ->
                frame.run(value).thenAccept { value ->
                    frame.run(time).long { time ->
                        frame.containerOrSender(selector).thenAccept {
                            it.forEachEntity {
                                if (value == null) {
                                    deleteFlag(key)
                                } else {
                                    setFlag(key, Data(value, survivalStamp = time * 50))
                                }
                            }
                        }
                    }
                }
            }
            return CompletableFuture.completedFuture(null)
        }

    }

    class DataAdd(val action: ParsedAction<*>, val value: ParsedAction<*>, val selector: ParsedAction<*>?) :
        ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            return frame.newFrame(action).run<Any>().thenAccept {
                val key = it.toString()
                frame.newFrame(value).run<Any>().thenAccept { value ->
                    frame.containerOrSender(selector).thenAccept {
                        it.forEachEntity {
                            this.updateFlag(key, getFlag(key)!!.data.increaseAny(value.toString()))
                        }
                    }
                }
            }
        }
    }

    companion object {


        /**
         * 设置数据
         * flag [key: action] to [value: action] <selector>
         *
         * 设置数据 并附带存活时间
         * flag [key: action] to [value: action] <timeout [time: action]>  <selector>
         *
         * 是否存在数据
         * flag [key: action] add [value: action]  <selector>
         *
         * 取数据 只取第一位是实体的数据
         * flag get [key: action] <selector:first>
         */
        @KetherParser(["flag", "data"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            val key = it.nextParsedAction()
            try {
                it.mark()
                when (it.expects("add", "set", "get", "to")) {
                    "set", "to" -> {
                        val value = it.nextParsedAction()
                        DataSet(key, value, it.nextParsedAction(arrayOf("timeout"), -1)!!, it.nextSelectorOrNull())
                    }

                    "get" -> DataGet(key, it.nextParsedAction(arrayOf("def", "--def"), "null")!!, it.nextSelectorOrNull())

                    "add" -> DataAdd(key, it.nextParsedAction(), it.nextSelectorOrNull())

                    else -> error("error of case!")
                }
            } catch (_: Throwable) {
                it.reset()
                DataGet(key, it.nextParsedAction(arrayOf("def", "--def"), "null")!!, it.nextSelectorOrNull())
            }
        }

    }

}