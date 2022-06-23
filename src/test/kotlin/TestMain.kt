import com.bh.planners.api.common.Demand
import kotlin.math.max

object TestMain {

    @JvmStatic
    fun main(args: Array<String>) {
        val data = Data(1000)
        println(data.end)
        println(data.countdown)
        println(data.isValid)
        Thread.sleep(2000)
        println(data.countdown)
        println(data.isValid)
    }

    class Data(val timeout: Long) {

        val create = System.currentTimeMillis()

        val end: Long
            get() = timeout + create

        val countdown: Long
            get() = max(end - System.currentTimeMillis(), 0)

        val isValid: Boolean
            get() = countdown > 0L

    }

}