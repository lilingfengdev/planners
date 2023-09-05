package com.bh.planners.core.kether.damageable

import com.bh.planners.core.feature.damageable.*
import com.bh.planners.core.kether.compat.attribute.AttributeBridge
import com.bh.planners.core.kether.nextOptionalParsedAction
import com.bh.planners.core.pojo.data.DataContainer.Companion.unsafeData
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.entity.Player
import taboolib.common.OpenResult
import taboolib.common5.cdouble
import taboolib.module.kether.*
import kotlin.collections.set

class ActionDamageable {

    companion object {

        @KetherParser(["arg", "argument"], namespace = DamageableScript.NAMESPACE)
        fun actionArgument() = combinationParser {
            it.group(text()).apply(it) { id ->
                now {
                    val damageable = getDamageable()
                    damageable.data[id]?.data
                }
            }
        }

        @KetherParser(["papi", "placeholder"], namespace = DamageableScript.NAMESPACE)
        fun actionPlaceholder() = scriptParser {
            val str = it.nextParsedAction()
            val defaultValue = it.nextOptionalParsedAction(arrayOf("def", "default"), "null")!!
            actionTake {
                run(str).str { s ->
                    run(defaultValue).thenApply {
                        val player = script().sender?.cast<Player>() ?: return@thenApply it
                        PlaceholderAPI.setPlaceholders(player, s)
                    }
                }
            }
        }

        @KetherParser(["tell", "send", "message"], namespace = DamageableScript.NAMESPACE)
        fun actionTell() = combinationParser {
            it.group(text()).apply(it) { str ->
                now { script().sender?.sendMessage(str.replace("@sender", script().sender?.name.toString())) }
            }
        }

        @KetherParser(["cancel"], namespace = DamageableScript.NAMESPACE)
        fun actionCancel() = scriptParser {
            actionNow {
                this.getDamageable().metaCancel = this.getDamageableMeta()
                null
            }
        }

        @KetherParser(["change"], namespace = DamageableScript.NAMESPACE)
        fun actionChange() = scriptParser {
            actionNow {
                val meta = getDamageableMeta()
                meta.changeSender()
                this.setSender(meta.sender)
            }
        }

        @KetherParser(["$"], namespace = DamageableScript.NAMESPACE)
        fun actionAttribute() = combinationParser {
            it.group(text()).apply(it) {
                now { AttributeBridge.INSTANCE?.get(getDamageableMeta().sender, it) }
            }
        }


        @KetherProperty(bind = Damageable::class)
        fun p0() = object : ScriptProperty<Damageable>("damageable.operator") {
            override fun read(instance: Damageable, key: String): OpenResult {
                val split = key.split(".")
                return when (split[0]) {
                    "data" -> OpenResult.successful(instance.data[split[1]]?.data)
                    "meta" -> OpenResult.successful(instance.metas.firstOrNull { it?.stream?.id == split[1] })
                    "damage" -> OpenResult.successful(instance.countDamage)
                    "is-cancel" -> OpenResult.successful(instance.metaCancel == null)
                    "cancel", "cancel-meta" -> OpenResult.successful(instance.metaCancel)
                    "damage-source", "source" -> OpenResult.successful(instance.damageSources[split[1]]?.value)
                    "attacker" -> OpenResult.successful(instance.attacker)
                    "defender", "victim" -> OpenResult.successful(instance.victim)
                    else -> OpenResult.failed()
                }
            }

            override fun write(instance: Damageable, key: String, value: Any?): OpenResult {
                val split = key.split(".")
                when (split[0]) {
                    "data" -> if (value != null) {
                        instance.data[key] = value.unsafeData()
                    }

                    "damage-source", "source" -> {
                        val id = split[1]
                        val source = instance.damageSources[id]
                        if (source == null) {
                            instance.damageSources[id] = Damageable.DamageSource(value.cdouble)
                        } else {
                            source.value = value.cdouble
                        }
                    }

                    else -> OpenResult.failed()
                }
                return OpenResult.successful()
            }

        }

    }


}