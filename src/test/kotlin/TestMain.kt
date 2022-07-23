import com.bh.planners.api.common.Baffle
import com.bh.planners.api.common.Demand
import com.bh.planners.core.effect.Target
import com.bh.planners.util.generatorId

object TestMain {

    const val NAME = "KunSs"

    @JvmStatic
    fun main(args: Array<String>) {
        val baffle = Baffle("aaa", 3000)
        println(baffle.countdown)
        println(baffle.next)

    }


}