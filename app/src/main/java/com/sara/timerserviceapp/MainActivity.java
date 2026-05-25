package com.sara.timerserviceapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    Button btnLaunch;
    Button btnEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLaunch = findViewById(R.id.btnLaunch);
        btnEnd = findViewById(R.id.btnEnd);

        // Permission notifications Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                Manifest.permission.POST_NOTIFICATIONS
                        },
                        100
                );
            }
        }

        btnLaunch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                startMyService();
            }
        });

        btnEnd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                stopMyService();
            }
        });
    }

    private void startMyService() {

        Intent intent =
                new Intent(
                        MainActivity.this,
                        TimerBackgroundService.class
                );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            startForegroundService(intent);

        } else {

            startService(intent);
        }
    }

    private void stopMyService() {

        Intent intent =
                new Intent(
                        MainActivity.this,
                        TimerBackgroundService.class
                );

        stopService(intent);
    }
}