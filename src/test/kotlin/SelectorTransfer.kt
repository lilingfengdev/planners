import java.util.concurrent.CompletableFuture

class SelectorTransfer(val source: String) {


    val selectorParsed = mutableListOf<ParsedSelector>()

    init {
        val split = source.split(" ")
        val values = mutableListOf<String>()
        var id: String? = null
        var at = false
        split.forEachIndexed { index, s ->
            if (s[0] == '@' || s.getOrNull(1) == '@') {
                // 保留上一条缓存
                if (at) {
                    selectorParsed += ParsedSelector(id!!, values.joinToString(" "))
                }
                at = true
                id = s.substring(s.indexOfFirst { it == '@' } + 1)
            }
            // 缓存参数
            else if (at) {
                values += s
            }
            if (index == split.lastIndex && at) {
                at = false
                selectorParsed += ParsedSelector(id!!, values.joinToString(" "))
            }
        }
    }

    fun run(): CompletableFuture<Void> {
        return process(0, CompletableFuture())
    }

    fun process(index: Int, future: CompletableFuture<Void>): CompletableFuture<Void> {

        if (index == selectorParsed.lastIndex) {
            future.complete(null)
            return future
        }
        process(index + 1, future)
        return future
    }


    class ParsedSelector(val namespace: String, val value: String)

}