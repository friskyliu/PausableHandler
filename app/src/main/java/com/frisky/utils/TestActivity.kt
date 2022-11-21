package com.frisky.utils

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.frisky.utils.pauseablehandler.R
import java.text.DecimalFormat

class TestActivity : AppCompatActivity() {
    private lateinit var btnStart: Button
    private lateinit var tvInfo: TextView
    private lateinit var pausableHandler: PausableHandler
    private lateinit var testHandler: Handler
    private var execStart = 0L
    private val decFormat = DecimalFormat("00000")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pausableHandler = PausableHandler(Looper.getMainLooper())
        testHandler = Handler(Looper.getMainLooper())

        btnStart = findViewById(R.id.btn_start)
        findViewById<Button>(R.id.btn_pause_auto).setOnClickListener {
            printInfo("PAUSE Start")
            pausableHandler.pause()
            printInfo("PAUSE End")
            testHandler.postDelayed({
                printInfo("RESUME Start")
                val pauseDuration = pausableHandler.resume()
                printInfo("RESUME End, pauseDuration:$pauseDuration")
            }, 3000)
        }

        btnStart.setOnClickListener {
            execStart = SystemClock.uptimeMillis()

            testPauseableHandler("1 -> ")
            testHandler.postDelayed({
                testPauseableHandler("2 -> ")
            }, 3000)
            testHandler.postDelayed({
                printInfo("-END-")
            }, 15000)
//            testHandler.postDelayed({
//                pausableHandler.removeCallbacksAndMessages(null)
//                printInfo("REMOVE ALL")
//            }, 6000)
        }

        tvInfo = findViewById(R.id.tv_info)
    }

    private fun testPauseableHandler(tag: String) {
        printInfo("$tag Start")

        pausableHandler.post {
            printInfo("$tag  Post")
        }

        pausableHandler.postDelayed({
            printInfo("$tag  postDelayed 2000")
        }, 2000)

        pausableHandler.postDelayed({
            printInfo("$tag  postDelayed 6000")
        }, 4000)

        pausableHandler.postDelayed({
            printInfo("$tag  postDelayed 12000")
        }, 8000)
    }

    override fun onResume() {
        pausableHandler.resume()
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        pausableHandler.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        pausableHandler.removeCallbacksAndMessages(null)
    }

    private fun printInfo(msg: String) {
        val tmp = "${decFormat.format(SystemClock.uptimeMillis() - execStart)}   ${pausableHandler.runningTaskSize()}/${pausableHandler.waitingTaskSize()}:$msg"
        val txt = "${tvInfo.text} $tmp \n"
        tvInfo.text = txt
        Log.e("PauseableHandler", tmp)
    }
}