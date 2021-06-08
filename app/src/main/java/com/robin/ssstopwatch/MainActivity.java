package com.robin.ssstopwatch;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.robin.ssstopwatch.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    static boolean isRunning;
    static boolean isPause;
    int milliSec;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setLifecycleOwner(this);
        isRunning = false;
        isPause = false;

        binding.btnReset.setOnClickListener(v -> {
            isPause = false;
            isRunning = false;
            binding.textTime.setText("000:00.00");
            binding.textRunning.setText("READY");
            binding.btnStopStartPause.setText("START");
            binding.btnStopStartPause.setBackgroundResource(R.drawable.rounded_frame_start);
            binding.btnReset.setVisibility(View.GONE);
        });

        binding.btnStopStartPause.setOnClickListener(v -> {
            if(!isRunning){ //초기상태일 때 시작버튼 누름
                isRunning = true;
                new StopWatchThread().start(); //스레드 실행
                binding.textRunning.setText("RUNNING");
                binding.btnStopStartPause.setText("STOP");
                binding.btnStopStartPause.setBackgroundResource(R.drawable.rounded_frame_stop);
            } else if(!isPause){ // 진행 중일 때 정지버튼 누름
                isPause = true;
                binding.btnReset.setVisibility(View.VISIBLE);
                binding.textRunning.setText("PAUSED");
                binding.btnStopStartPause.setText("RESUME");
                binding.btnStopStartPause.setBackgroundResource(R.drawable.rounded_frame_restart);
            } else { //정지 상태일 때 스탑워치 재개
                isPause = false;
                binding.btnReset.setVisibility(View.GONE);
                binding.textRunning.setText("RUNNING");
                binding.btnStopStartPause.setText("STOP");
                binding.btnStopStartPause.setBackgroundResource(R.drawable.rounded_frame_stop);
            }
        });
    }


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (MainActivity.isRunning) {
                int milliSecond = msg.arg1;
                int mSec = milliSecond % 100;
                int sec = (milliSecond / 100) % 60;
                int min = (milliSecond / 100) / 60;
                @SuppressLint("DefaultLocale") String time = String.format("%03d:%02d.%02d", min, sec, mSec);
                binding.textTime.setText(time);
            } else {
                binding.textTime.setText("000:00:00");

            }

        }
    };

    class StopWatchThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (MainActivity.isRunning) {
                for (; !MainActivity.isPause && MainActivity.isRunning; milliSec++) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Message message = new Message();
                    message.arg1 = milliSec;
                    handler.sendMessage(message);
                }
            }
            milliSec = 0;
        }
    }
}
