import com.bh.planners.api.common.Demand
import com.bh.planners.core.effect.Target
import com.bh.planners.util.generatorId

object TestMain {

    @JvmStatic
    fun main(args: Array<String>) {
        val demand = Demand("REDSTONE 0 0.1 0 -color 255,0,0 -posY \\-1.5 -start 0 -angle 540 -period 1 -slope 0.004 -step 12 -radius 1.3 -speed 0.0 -count 1 -@self")
        println(demand)
    }

    class TestTarget : Target {
        override fun toLocal(): String {
            return "test"
        }

    }

}