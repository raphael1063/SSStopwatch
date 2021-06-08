package com.robin.ssstopwatch

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.robin.ssstopwatch.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {
    var milliSec = 0
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        with(binding) {
            lifecycleOwner = this@MainActivity
            btnReset.setOnClickListener {
                isPause = false
                isRunning = false
                textTime.text = "000:00.00"
                textRunning.text = getString(R.string.ready)
                btnStopStartPause.text = getString(R.string.start)
                btnStopStartPause.setBackgroundResource(R.drawable.rounded_frame_start)
                btnReset.visibility = View.GONE
            }
            btnStopStartPause.setOnClickListener {
                if (!isRunning) { //초기상태일 때 시작버튼 누름
                    isRunning = true
                    StopWatchThread().start() //스레드 실행
                    textRunning.text = getString(R.string.running)
                    btnStopStartPause.text = getString(R.string.stop)
                    btnStopStartPause.setBackgroundResource(R.drawable.rounded_frame_stop)
                } else if (!isPause) { // 진행 중일 때 정지버튼 누름
                    isPause = true
                    btnReset.visibility = View.VISIBLE
                    textRunning.text = getString(R.string.paused)
                    btnStopStartPause.text = getString(R.string.resume)
                    btnStopStartPause.setBackgroundResource(R.drawable.rounded_frame_restart)
                } else { //정지 상태일 때 스탑워치 재개
                    isPause = false
                    btnReset.visibility = View.GONE
                    textRunning.text = getString(R.string.running)
                    btnStopStartPause.text = getString(R.string.stop)
                    btnStopStartPause.setBackgroundResource(R.drawable.rounded_frame_stop)
                }
            }
        }
        }


    @SuppressLint("HandlerLeak")
    var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (isRunning) {
                val milliSecond = msg.arg1
                val mSec = milliSecond % 100
                val sec = milliSecond / 100 % 60
                val min = milliSecond / 100 / 60
                 val time =
                    String.format("%03d:%02d.%02d", min, sec, mSec, Locale.US)
                binding.textTime.text = time
            } else {
                binding.textTime.text = "000:00:00"
            }
        }
    }

    internal inner class StopWatchThread : Thread() {
        override fun run() {
            super.run()
            while (isRunning) {
                while (!isPause && isRunning) {
                    try {
                        sleep(10)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                    val message = Message()
                    message.arg1 = milliSec
                    handler.sendMessage(message)
                    milliSec++
                }
            }
            milliSec = 0
        }
    }

    companion object {
        private var isRunning = false
        private var isPause = false
    }
}