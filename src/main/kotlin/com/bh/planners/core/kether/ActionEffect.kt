package com.bh.planners.core.kether

import com.bh.planners.core.skill.effect.EffectArc
import com.bh.planners.core.skill.effect.Effects
import taboolib.library.kether.ArgTypes
import taboolib.module.kether.KetherParser
import taboolib.module.kether.expects
import taboolib.module.kether.scriptParser
import taboolib.module.kether.switch

object ActionEffect {

    /**
     * effect <loader> <option: string>
     * effect line "FLAME 0 0 0 -speed 1.0 -count 10 -@self"
     */
    @KetherParser(["effect"], namespace = NAMESPACE)
    fun parser() = scriptParser {
        try {
            it.mark()
            val expect = it.expects(*Effects.loaderKeys.toTypedArray())
            val effectLoader = Effects.get(expect)
            effectLoader.parser(it)
        } catch (ex: Exception) {
            it.reset()
            throw ex
        }
    }

}
