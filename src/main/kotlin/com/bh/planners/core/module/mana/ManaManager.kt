package com.bh.planners.core.module.mana

import com.bh.planners.core.pojo.player.PlayerProfile
import taboolib.common.util.unsafeLazy
import taboolib.library.reflex.Reflex.Companion.invokeConstructor
import taboolib.module.configuration.ConfigNode

interface ManaManager {

    companion object {

        @ConfigNode("mana.drive", bind = "module.yml")
        val drive = "default"

        val INSTANCE by unsafeLazy {
            when (drive) {
                "default" -> DefaultManaManager()
                else -> try {
                    Class.forName(drive).invokeConstructor() as ManaManager
                } catch (e: Exception) {
                    e.printStackTrace()
                    ErrorManaManager()
                }
            }
        }

    }

    fun onEnable()

    fun onDisable()

    fun getRegainMana(profile: PlayerProfile): Double

    fun getMaxMana(profile: PlayerProfile): Double

    fun getMana(profile: PlayerProfile): Double

    fun addMana(profile: PlayerProfile, value: Double)

    fun takeMana(profile: PlayerProfile, value: Double)

    fun setMana(profile: PlayerProfile, value: Double)


}