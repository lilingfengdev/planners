package com.bh.planners.core.kether

import com.bh.planners.api.PlannersAPI
import com.bh.planners.api.PlannersAPI.plannersProfile
import org.bukkit.entity.Player
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionSkill {


    class ActionSkillMeta(val skill: ParsedAction<*>, val type: Type) : ScriptAction<Any>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Any> {
            return frame.newFrame(skill).run<Any>().thenApply { skill ->
                val player = frame.script().sender!!.castSafely<Player>()!!
                val playerSkill = player.plannersProfile.getSkill(skill.toString()) ?: error("Not skill $skill")
                when (type) {
                    Type.KEY -> playerSkill.instance.key
                    Type.NAME -> playerSkill.instance.option.name
                    Type.LEVEL -> playerSkill.level
                    Type.LEVEL_MAX -> playerSkill.instance.option.levelCap
                    Type.SHORTCUT_KEY -> playerSkill.keySlot?.name ?: "暂无"
                }
            }
        }

    }

    class ActionSkillCast(val action: ParsedAction<*>, val markAction: ParsedAction<*>) : ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            frame.newFrame(action).run<String>().thenAccept { skill ->
                frame.newFrame(markAction).run<Boolean>().thenAccept { mark ->
                    val player = frame.script().sender!!.castSafely<Player>()!!
                    PlannersAPI.cast(player, skill, mark)
                }
            }
            return CompletableFuture.completedFuture(null)
        }
    }


    enum class Type {
        KEY, NAME, LEVEL, LEVEL_MAX, SHORTCUT_KEY
    }

    companion object {

        @KetherParser(["skill"], namespace = NAMESPACE)
        fun parser() = scriptParser {
            val key = it.next(ArgTypes.ACTION)
            it.switch {
                case("level") { ActionSkillMeta(key, Type.LEVEL) }
                case("name") { ActionSkillMeta(key, Type.NAME) }
                case("key") { ActionSkillMeta(key, Type.KEY) }
                case("level-max", "level-cap") { ActionSkillMeta(key, Type.LEVEL_MAX) }
                case("shortcut") { ActionSkillMeta(key, Type.SHORTCUT_KEY) }
            }

        }

    }
}
