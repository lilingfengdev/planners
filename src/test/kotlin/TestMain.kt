import com.bh.planners.core.effect.Target
import com.bh.planners.util.generatorId

object TestMain {

    @JvmStatic
    fun main(args: Array<String>) {
        val demand = Demand("END_ROD 0 -3.0 0 -radius 0.5 -sample 100 -speed 0.5 -count 0 -@offset 2,5,3")
        println(demand)
    }

    class TestTarget : Target {
        override fun toLocal(): String {
            return "test"
        }

    }

}