package lai.so.myapplication

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.squareup.moshi.Moshi
import okhttp3.*
import okio.ByteString

class MainActivity : AppCompatActivity() {
    lateinit var mRSSValueView: TextView
    lateinit var mTotalValueView: TextView
    lateinit var mUsedValueView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mRSSValueView = findViewById(R.id.rss_value) as TextView
        mUsedValueView = findViewById(R.id.used_value) as TextView
        mTotalValueView = findViewById(R.id.total_value) as TextView

        start("ws://10.0.2.2:8080/")
    }

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

        override fun onMessage(webSocket: WebSocket?, bytes: ByteString?) {
            println("MESSAGE: " + bytes!!.hex())
        }

        override fun onClosing(webSocket: WebSocket?, code: Int, reason: String?) {
            webSocket!!.close(1000, null)
            println("CLOSE: $code $reason")
        }

        override fun onFailure(webSocket: WebSocket?, t: Throwable?, response: Response?) {
            t!!.printStackTrace()
        }
    }
}

