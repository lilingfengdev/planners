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
        val listOf = mutableListOf(
            Data(10),
            Data(8),
            Data(2),
            Data(52),
            Data(45),
            Data(1),
            Data(3),
        )
        listOf.sortBy { it.priority }
        println(listOf)
    }

    class Data(val priority: Int)
}