package com.bh.planners.core.kether.compat.germplugin

import com.germ.germplugin.api.dynamic.animation.GermAnimationPart

interface IEffectAnimation {

    fun create(): GermAnimationPart<*>

}