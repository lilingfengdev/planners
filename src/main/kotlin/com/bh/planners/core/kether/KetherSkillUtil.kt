package com.bh.planners.core.kether

import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.PlannersAPI.plannersProfileIsLoaded
import com.bh.planners.core.pojo.player.PlayerJob
import com.bh.planners.core.pojo.player.PlayerProfile
import taboolib.library.kether.ParsedAction
import taboolib.library.kether.QuestContext
import taboolib.module.kether.actionFuture
import taboolib.module.kether.actionTake
import taboolib.module.kether.run
import taboolib.module.kether.str
import java.util.concurrent.CompletableFuture


fun actionSkillNow(func: QuestContext.Frame.(PlayerJob.Skill) -> Any?) = actionFuture { future ->
    future.complete(func(this,this.skill()))
}

fun actionSkillNow(of: ParsedAction<*>?, func: QuestContext.Frame.(PlayerJob.Skill) -> Any?) = actionFuture { future ->
    if (of != null) {
        this.run(of).str { skill ->
            future.complete(func(this,this.bukkitPlayer()?.plannersProfile?.getSkill(skill) ?: error("No skill $skill")))
        }
    } else {
        future.complete(func(this,this.skill()))
    }
}

fun actionSkillFuture(of: ParsedAction<*>?, func: QuestContext.Frame.(PlayerJob.Skill) -> CompletableFuture<*>) = actionFuture { future ->
    if (of != null) {
        run(of).str { skill ->
            func(this,this.bukkitPlayer()?.plannersProfile?.getSkill(skill) ?: error("No skill $skill")).thenAccept {
                future.complete(it)
            }
        }
    } else {
        func(this,this.skill()).thenAccept {
            future.complete(it)
        }
    }
}

fun actionProfileNow(func: QuestContext.Frame.(PlayerProfile) -> Any?) = actionFuture { future ->
    val player = this.bukkitPlayer() ?: error("No player selected.")
    if (!player.plannersProfileIsLoaded) {
        future.complete("__LOADED__")
        return@actionFuture future
    }
    future.complete(func(this, player.plannersProfile))
}

fun actionProfileNow(action: ParsedAction<*>,func: QuestContext.Frame.(value: Any?,profile: PlayerProfile) -> Any?) = actionFuture { future ->
    val player = this.bukkitPlayer() ?: error("No player selected.")
    if (!player.plannersProfileIsLoaded) {
        future.complete("__LOADED__")
        return@actionFuture future
    }
    this.run(action).thenAccept {
        future.complete(func(this,it, player.plannersProfile))
    }
}
fun actionProfileTake(func: QuestContext.Frame.(profile: PlayerProfile) -> Unit) = actionTake {
    val player = this.bukkitPlayer() ?: error("No player selected.")
    if (!player.plannersProfileIsLoaded) {
        return@actionTake CompletableFuture.completedFuture(null)
    }
    func(this, player.plannersProfile)

    return@actionTake CompletableFuture.completedFuture(null)
}
fun actionProfileTake(action: ParsedAction<*>, func: QuestContext.Frame.(value: Any, profile: PlayerProfile) -> Unit) = actionTake {
    val player = this.bukkitPlayer() ?: error("No player selected.")
    if (!player.plannersProfileIsLoaded) {
        return@actionTake CompletableFuture.completedFuture(null)
    }

    this.run(action).thenAccept {
        func(this, it!!, player.plannersProfile)
    }

    return@actionTake CompletableFuture.completedFuture(null)
}
