import com.bh.planners.api.common.Demand

object TestMain {

    const val NAME = "KunSs"

    @JvmStatic
    fun main(args: Array<String>) {
    }


    val FILTER_RULES = listOf(
        Regex("#?+[a-zA-Z0-9]+"),
        Regex("§+[a-zA-Z0-9%]"),
        Regex("[^0-9+--.]"),
    )

    fun getNumber(string: String): String {
        var prey = string
        FILTER_RULES.forEach { prey = prey.replace(it, "") }
        return prey.ifEmpty { "0.0" }
    }
}