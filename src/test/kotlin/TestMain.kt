import com.bh.planners.api.common.Demand
import kotlinx.coroutines.*
import java.time.Duration

object TestMain {

    const val NAME = "KunSs"


    fun String.eval(amount: Double): Double {
        return if (this.last() == '%') {
            println(this.substring(0,this.lastIndex))
            amount * (this.substring(0, this.lastIndex).toDouble() / 100)
        } else {
            this.toDouble()
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val demand = Demand("flame :key 123 456 :key1 sadasd")
        println(demand)
    }

    class Data(val priority: Int)
}