package com.bh.planners.core.pojo

import com.bh.planners.api.EntityAPI.getDataContainer
import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.script.ScriptLoader
import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.getLivingEntity
import com.bh.planners.core.kether.LazyGetter
import com.bh.planners.core.pojo.data.DataContainer
import com.bh.planners.core.pojo.data.DataContainer.Companion.unsafeData
import com.bh.planners.core.pojo.player.PlayerJob
import com.bh.planners.core.pojo.player.PlayerProfile
import com.bh.planners.util.runKetherThrow
import com.bh.planners.util.toProxyCommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.console
import taboolib.module.kether.*
import java.util.*

abstract class Context(val sender: Target) {

    val id = UUID.randomUUID().toString()

    var closed = false

    val proxySender: ProxyCommandSender
        get() = sender.toProxyCommandSender() ?: console()

    var origin = sender

    var ketherScriptContext: ScriptContext? = null

    val player: Player?
        get() = sender.getLivingEntity() as? Player

    val profile: PlayerProfile?
        get() = player?.plannersProfile

    val dataContainer: DataContainer
        get() = player?.getDataContainer() ?: DataContainer()

    val variables = DataContainer()

    open var stackId = "Unknown"

    protected fun toLazyVariable(variable: Skill.Variable): LazyGetter<*> {
        return LazyGetter {
            runKetherThrow(stackId) {
                ScriptLoader.createScript(this, variable.expression) { }.get()
            }
        }
    }

    abstract class SourceImpl(sender: Target) : Context(sender) {

        abstract val sourceId: String


    }

    open class Impl(sender: Target, val skill: Skill) : SourceImpl(sender) {

        open val playerSkill: PlayerJob.Skill = profile?.getSkill(skill.key)!!

        override val sourceId: String = skill.key

        val script = skill.script

        val scriptMode = script.mode

        val scriptAction = script.action

        override var stackId: String = "Skill: $id"

        init {
            skill.option.variables.forEach {
                variables[it.key] = toLazyVariable(it).unsafeData()
            }
        }

    }

    open class Impl0(sender: Target) : Context(sender)

    open class Impl1(sender: Target, skill: Skill, val level: Int) : Session(sender, skill) {

        override val playerSkill = PlayerJob.Skill(-1, skill.key, level, null)

    }
}