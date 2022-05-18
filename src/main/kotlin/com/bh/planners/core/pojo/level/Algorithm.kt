package com.bh.planners.core.pojo.level

import java.util.concurrent.CompletableFuture

abstract class Algorithm {

    abstract val maxLevel: Int

    abstract fun getExp(level: Int): CompletableFuture<Int>
}
