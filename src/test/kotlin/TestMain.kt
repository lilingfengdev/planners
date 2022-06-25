
object TestMain {

    @JvmStatic
    fun main(args: Array<String>) {

        //  pos2 [ -radius 4 -@dot 100 ]
        val demand = Demand("FLAME 0 0 0 pos1 { -@c-dot 3,0 } pos2 { -@c-dot 4,0 }")
        println(demand)
    }

}