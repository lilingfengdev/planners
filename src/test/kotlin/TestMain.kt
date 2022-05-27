import com.bh.planners.api.particle.Demand

object TestMain {

    @JvmStatic
    fun main(args: Array<String>) {
        val demand = Demand("FLAME 0 0 1 -speed 1.0 -count a -@self")
        println(demand)
    }

}