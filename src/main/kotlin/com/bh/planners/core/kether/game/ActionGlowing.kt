package com.bh.planners.core.kether.game

import com.bh.planners.api.common.SimpleTimeoutTask
import com.bh.planners.api.common.SimpleUniqueTask
import com.bh.planners.core.kether.*
import com.bh.planners.core.kether.util.GlowUtil
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Entity
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.scoreboard.Team
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.function.info
import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionGlowing : ScriptAction<Void>() {

    lateinit var tick: ParsedAction<*>
    lateinit var value: ParsedAction<*>
    lateinit var color: ParsedAction<*>
    var selector: ParsedAction<*>? = null

    fun execute(entity: Entity, value: Boolean, color: ChatColor, tick: Long) {
        if (value) {
            setColor(entity, color)
            if (tick != -1L) {
                SimpleUniqueTask.submit("@glowing:${entity.uniqueId}", tick) {
                    unsetColor(entity)
                }
            }
        } else {
            unsetColor(entity)
        }
    }

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {

        frame.run(tick).long { tick ->
            frame.run(value).bool { glowing ->
                frame.run(color).str {
                    val chatColor = ChatColor.valueOf(it.toUpperCase())
                    if (selector != null) {
                        frame.execEntity(selector!!) {
                            execute(this, glowing, chatColor, tick)
                        }
                    } else {
                        execute(frame.bukkitPlayer() ?: return@str, glowing, chatColor, tick)
                    }
                }
            }
        }

        return CompletableFuture.completedFuture(null)
    }

    companion object {

        val colorTeams = mutableMapOf<ChatColor, Team>()

        fun mainScoreboard(): Scoreboard {
            return Bukkit.getServer().scoreboardManager!!.mainScoreboard
        }

        @Awake(LifeCycle.ENABLE)
        fun initColor() {
            ChatColor.values().forEachIndexed { index, chatColor ->
                mainScoreboard().getTeam("planners-$index")?.unregister()
                colorTeams[chatColor] = mainScoreboard().registerNewTeam("planners-$index").also {
                    it.color = chatColor
                    it.prefix = chatColor.toString()
                }
            }
        }

        fun setColor(entity: Entity, color: ChatColor) {
            colorTeams[color]?.addEntry(entity.uniqueId.toString())
            entity.isGlowing = true
        }

        fun unsetColor(entity: Entity) {
            entity.isGlowing = false
        }

        /**
         * 设置目标发光，-1为永久值 需要取消
         * glowing <timeout: action(-1)> <value: action(true)> <selector>
         * glowing <timeout> <value> <color> <selector>
         */
        @KetherParser(["glowing"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            ActionGlowing().apply {
                this.tick = it.nextArgumentAction(arrayOf("tick", "time", "timeout"), -1)!!
                this.value = it.nextArgumentAction(arrayOf("value"), true)!!
                this.color = it.nextArgumentAction(arrayOf("color"), ChatColor.WHITE)!!
                this.selector = it.nextSelectorOrNull()
            }
        }

    }


}