object TestMain {

    @JvmStatic
    fun main(args: Array<String>) {
        val demand = Demand("-step 10 -amount \\-10")
        println(demand)
        println("\\-10")
        println("\\-10".startsWith("\\-"))
    }

}