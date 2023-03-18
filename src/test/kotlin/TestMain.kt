

object TestMain {

    @JvmStatic
    fun main(args: Array<String>) {
        SelectorTransfer(":@ttt ab 1 4 5 @ass 45ds sa").run().thenAccept {
            println("===")
        }
    }

}