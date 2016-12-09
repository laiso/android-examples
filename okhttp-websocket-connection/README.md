- [Web Sockets now shipping in OkHttp 3\.5\! – Square Corner Blog – Medium](https://medium.com/square-corner-blog/web-sockets-now-shipping-in-okhttp-3-5-463a9eec82d1#.l3katr74e)

## Demo
![cap](https://cloud.githubusercontent.com/assets/39830/21057267/4df2b1e0-be7c-11e6-85bb-6424d7950ce7.gif)

```kotlin
fun start(url: String) {
        val client = OkHttpClient()
        val request = Request.Builder()
                .url(url)
                .build()
        client.newWebSocket(request, MyListener(this))
        client.dispatcher().executorService().shutdown()
    }

    class MyListener(private val activity: MainActivity) : WebSocketListener() {
        data class Stat(
                val rss: Int,
                val heapTotal: Int,
                val heapUsed: Int
        )

        private val moshi = Moshi.Builder().build()
        private val jsonAdapter = moshi.adapter(Stat::class.java)

        override fun onOpen(webSocket: WebSocket?, response: Response?) {
            println("OPEN: $response")
        }

        override fun onMessage(webSocket: WebSocket?, text: String?) {
            println("MESSAGE: " + text!!)
            val stat = jsonAdapter.fromJson(text)
            activity.runOnUiThread {
                activity.mRSSValueView.text = stat.rss.toString()
                activity.mUsedValueView.text = stat.heapUsed.toString()
                activity.mTotalValueView.text = stat.heapTotal.toString()
            }
        }
}
```

## JSON Decoding
- [Moshi](https://github.com/square/moshi)

## websocket-server
- [serverstats\-express\_3](https://github.com/websockets/ws/tree/master/examples/serverstats-express_3)
