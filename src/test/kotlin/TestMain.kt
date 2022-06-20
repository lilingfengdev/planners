import com.bh.planners.api.common.Demand

object TestMain {

    @JvmStatic
    fun main(args: Array<String>) {
        val demand = Demand("REDSTONE 0.5 0.5 0.5 -@self -posY 0.3 -angle 180 -radius 5 -count 10 -step 1 -!slope -0.015 -start 270")
        println(demand.dataMap)
    }

}