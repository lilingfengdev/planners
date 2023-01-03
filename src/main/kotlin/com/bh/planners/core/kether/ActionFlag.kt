package com.bh.planners.core.kether

import com.bh.planners.api.EntityAPI.getDataContainer
import com.bh.planners.api.EntityAPI.getFlag
import com.bh.planners.api.EntityAPI.setFlag
import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.setFlag
import com.bh.planners.core.pojo.data.Data
import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionFlag {

    class DataGet(val action: ParsedAction<*>, val selector: ParsedAction<*>?) : ScriptAction<Any>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Any> {
            val future = CompletableFuture<Any>()
            frame.newFrame(action).run<Any>().thenAccept {
                val key = it.toString()
                if (selector != null) {
                    frame.createContainer(selector).thenAccept {
                        val entityTarget = it.firstEntityTarget()
                        if (entityTarget != null) {
                            future.complete(entityTarget.getFlag(key))
                        } else {
                            future.complete(null)
                        }
                    }
                } else {
                    future.complete(frame.bukkitPlayer()?.getFlag(key))
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
            return frame.newFrame(action).run<Any>().thenAccept { key ->
                frame.newFrame(value).run<Any>().thenAccept { value ->

                    frame.newFrame(time).run<Any>().thenAccept { time ->
                        if (selector != null) {
                            frame.execEntity(selector) {
                                setFlag(key.toString(), Data(value, survivalStamp = Coerce.toLong(time) * 50))
                            }
                        } else {
                            val profile = frame.bukkitPlayer()!!.plannersProfile
                            profile.setFlag(key.toString(), Data(value, survivalStamp = Coerce.toLong(time) * 50))
                        }
                    }
                }
            }
        }

    }

    class DataAdd(val action: ParsedAction<*>, val value: ParsedAction<*>, val selector: ParsedAction<*>?) :
        ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            return frame.newFrame(action).run<Any>().thenAccept {
                val key = it.toString()
                frame.newFrame(value).run<Any>().thenAccept { value ->
                    if (selector != null) {
                        frame.execEntity(selector) {
                            val dataContainer = getDataContainer()
                            if (dataContainer.containsKey(key)) {
                                dataContainer.update(key, dataContainer[key]!!.increaseAny(value.toString()))
                            }
                        }
                    } else {
                        val dataContainer = frame.bukkitPlayer()!!.getDataContainer()
                        if (dataContainer.containsKey(key)) {
                            dataContainer.update(key, dataContainer[key]!!.increaseAny(value.toString()))
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
                when (it.expects("add", "set", "get","to")) {
                    "set", "to" -> {
                        val value = it.nextParsedAction()
                        DataSet(key, value, it.tryGet(arrayOf("timeout"),-1)!!, it.selectorAction())
                    }

                    "get" -> DataGet(key, it.selectorAction())

                    "add" -> DataAdd(key, it.nextParsedAction(), it.selectorAction())

                    else -> error("error of case!")
                }
            } catch (_: Throwable) {
                it.reset()
                DataGet(key, it.selectorAction())
            }
        }

    }

}