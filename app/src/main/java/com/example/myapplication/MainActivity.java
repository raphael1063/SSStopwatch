package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.textTime)
    TextView textTime;
    @BindView(R.id.pauseRestart)
    Button pauseRestart;
    @BindView(R.id.stopStart)
    Button stopStart;

    static boolean isRunning;
    static boolean isPause;
    int milliSec;
    String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        pauseRestart.setVisibility(View.GONE);
    }

    @OnClick({R.id.pauseRestart, R.id.stopStart})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.pauseRestart:
                MainActivity.isPause = !MainActivity.isPause;
                Log.e(TAG, "isPause : " + MainActivity.isPause);
                break;
            case R.id.stopStart:
                MainActivity.isRunning = !MainActivity.isRunning;
                Log.e(TAG, "isRunning : " + MainActivity.isRunning);
                if (!MainActivity.isRunning) {
                    stopStart.setText("시작");
                    isPause = true;
                    pauseRestart.setVisibility(View.GONE);
                    textTime.setText("000:00:00");
                } else {
                    TimerThread timerThread = new TimerThread();
                    timerThread.start();

                    stopStart.setText("정지");
                    isPause = false;
                    pauseRestart.setVisibility(View.VISIBLE);
                }

                break;
        }
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
                String time = String.format("%03d:%02d:%02d", min, sec, mSec);
                textTime.setText(time);
            } else {
                textTime.setText("000:00:00");

            }

        }
    };

    class TimerThread extends Thread {


        @Override
        public void run() {
            super.run();
            while (MainActivity.isRunning) {
                Log.e(TAG, "Thread is running...");
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
            Log.e(TAG, "TimerThread 종료");
            milliSec = 0;
        }
    }
}
