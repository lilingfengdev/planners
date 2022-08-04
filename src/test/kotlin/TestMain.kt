import com.bh.planners.api.common.Demand

object TestMain {

    const val NAME = "KunSs"

    @JvmStatic
    fun main(args: Array<String>) {
        val demand = Demand(":@range 3 :their @amount 3")
        println(demand)
    }


}