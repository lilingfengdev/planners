package com.bh.planners.core.kether.damageable

import com.bh.planners.core.feature.damageable.Damageable
import com.bh.planners.core.feature.damageable.getDamageable
import com.bh.planners.core.feature.damageable.getDamageableMeta
import com.bh.planners.core.kether.DAMAGEABLE_NAMESPACE
import com.bh.planners.core.kether.NAMESPACE
import com.bh.planners.core.kether.damageableNamespaces
import com.bh.planners.core.pojo.data.DataContainer.Companion.unsafeData
import taboolib.common.OpenResult
import taboolib.common5.cdouble
import taboolib.module.kether.*

class ActionDamageable {

    companion object {

        @KetherParser(["cancel"], namespace = DAMAGEABLE_NAMESPACE)
        fun actionCancel() = scriptParser {
            actionNow {
                this.getDamageable().metaCancel = this.getDamageableMeta()
                null
            }
        }

        @KetherProperty(bind = Damageable::class)
        fun p0() = object : ScriptProperty<Damageable>("damageable.operator") {
            override fun read(instance: Damageable, key: String): OpenResult {
                val split = key.split(".")
                return when(split[0]) {
                    "data" -> OpenResult.successful(instance.data[split[1]]?.data)
                    "meta" -> OpenResult.successful(instance.metas.firstOrNull { it?.stream?.id == split[1] })
                    "damage" -> OpenResult.successful(instance.countDamage)
                    "is-cancel" -> OpenResult.successful(instance.metaCancel == null)
                    "cancel","cancel-meta" -> OpenResult.successful(instance.metaCancel)
                    "damage-source","source" -> OpenResult.successful(instance.damageSources[split[1]]?.value)
                    else -> OpenResult.failed()
                }
            }

            override fun write(instance: Damageable, key: String, value: Any?): OpenResult {
                val split = key.split(".")
                when(split[0]) {
                    "data" -> if (value != null) {
                        instance.data.set(key,value.unsafeData())
                    }
                    "damage-source","source" -> {
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