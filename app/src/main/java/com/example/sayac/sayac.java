package com.example.sayac;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

public class sayac extends AppCompatActivity implements View.OnClickListener {

    private int lastResult;
    TextView txtResult;
    MaterialButton buttonMinus, buttonPlus, buttonReset;

    Vibrator vibrator;
    MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sayac);

        txtResult = findViewById(R.id.txt_result);

        assignId(buttonMinus, R.id.button_minus);
        assignId(buttonPlus, R.id.button_plus);
        assignId(buttonReset, R.id.button_reset);

        initCounter();

        // Shake Phone

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor sensorShake = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        SensorEventListener sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (sensorEvent != null) {
                    float x = sensorEvent.values[0];
                    float y = sensorEvent.values[1];
                    float z = sensorEvent.values[2];

                    if (x > 2 || x < -2 || y > 10 || y < -10 || z > 2 || z < -2) {
                        lastResult = 0;
                        txtResult.setText(lastResult + "");
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
        sensorManager.registerListener(sensorEventListener, sensorShake, sensorManager.SENSOR_DELAY_NORMAL);

        //Vibrator
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (Build.VERSION.SDK_INT >= 26) {
            if (lastResult > 100) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (lastResult < -100) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            }
        } else {
            vibrator.vibrate(500);
        }


    }

    public void playAudio() {
        try {
            mMediaPlayer = MediaPlayer.create(this, R.raw.cat);
            mMediaPlayer.setLooping(false);
            Log.e("beep", "started0");
            mMediaPlayer.start();
            Log.e("beep", "started1");
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer arg0) {
                    finish();
                }
            });
        } catch (Exception e) {
            Log.e("beep", "error: " + e.getMessage(), e);
        }
    }

    public void playAlertTone(final Context context) {
        Thread t = new Thread() {
            public void run() {
                MediaPlayer player = null;
                int countBeep = 0;
                while (countBeep < 2) {
                    player = MediaPlayer.create(context, R.raw.cat);
                    player.start();
                    countBeep += 1;
                    try {
                        // 100 milisecond is duration gap between two beep
                        Thread.sleep(player.getDuration() + 100);
                        player.release();
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        };
        t.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    void assignId(MaterialButton btn, int id) {
        btn = findViewById(id);
        btn.setOnClickListener(this);
    }


    void initCounter() {
        lastResult = 0;
        txtResult.setText(lastResult + "");
    }

    void minusCounter() {
        lastResult--;
        if (lastResult < -100) {
            lastResult = -100;
            Toast.makeText(getApplicationContext(), "Meoww", 5000).show();
            playAlertTone(getApplicationContext());
        }
        txtResult.setText(lastResult + "");
    }

    void plusCounter() {
        lastResult++;
        if (lastResult > 100) {
            lastResult = 100;
            Toast.makeText(getApplicationContext(), "Meoww", 5000).show();
            playAlertTone(getApplicationContext());
        }
        txtResult.setText(lastResult + "");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_minus:
                minusCounter();
                break;
            case R.id.button_plus:
                plusCounter();
                break;
            case R.id.button_reset:
                initCounter();
                break;
        }
    }


    // Sound Button Action
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action, keycode;

        action = event.getAction();
        keycode = event.getKeyCode();

        switch (keycode) {
            case KeyEvent.KEYCODE_VOLUME_UP: {
                if (KeyEvent.ACTION_UP == action) {
                    lastResult += 5;
                    if (lastResult > 100) {
                        lastResult = 100;
                    }
                    txtResult.setText(lastResult + "");
                    break;
                }
            }
            case KeyEvent.KEYCODE_VOLUME_DOWN: {
                if (KeyEvent.ACTION_UP == action) {
                    lastResult -= 5;
                    if (lastResult < -100) {
                        lastResult = -100;
                    }
                    txtResult.setText(lastResult + "");
                    break;
                }
            }
        }

        return super.dispatchKeyEvent(event);
    }

    ;

}