package com.bh.planners.core.timer

import com.bh.planners.core.effect.Target
import com.bh.planners.core.pojo.Context

class TimerContext(sender: Target, template: Template) : Context.SourceImpl(sender) {

    override val sourceId: String = template.id

    override var stackId: String = "Timer: $sourceId"
}