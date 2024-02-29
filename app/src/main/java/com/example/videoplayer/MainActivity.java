package com.example.videoplayer;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.videoplayer.R;

public class MainActivity extends AppCompatActivity {

    private VideoView videoView;
    private Button playButton;
    private Button pauseButton;
    private Button stopButton;
    private TextView statusTextView;
    private SeekBar seekBar;
    private int currentPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView = findViewById(R.id.videoView1);
        playButton = findViewById(R.id.button1);
        pauseButton = findViewById(R.id.button);
        stopButton = findViewById(R.id.button2);
        statusTextView = findViewById(R.id.statusTextView);
        seekBar = findViewById(R.id.seekBar);

        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.video;
        Uri uri = Uri.parse(videoPath);
        videoView.setVideoURI(uri);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playVideo();
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseVideo();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopVideo();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    videoView.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Nu este nevoie să implementăm ceva aici
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Nu este nevoie să implementăm ceva aici
            }
        });
    }

    private void playVideo() {
        if (!videoView.isPlaying()) {
            if (currentPosition > 0) {
                videoView.seekTo(currentPosition);
            }
            videoView.start();
            statusTextView.setText("Playing");
            stopButton.setEnabled(true);

            // Setăm durata totală a progresului la valoarea maximă a video-ului
            seekBar.setMax(videoView.getDuration());

            // Actualizăm progresul în funcție de locația curentă a videoclipului
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (videoView.isPlaying()) {
                        int currentPosition = videoView.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                        new Handler().postDelayed(this, 100); // Actualizăm progresul la fiecare 100 de milisecunde
                    }
                }
            }, 100);
        }
    }

    private void pauseVideo() {
        if (videoView.isPlaying()) {
            videoView.pause();
            statusTextView.setText("Paused");
        }
    }

    private void stopVideo() {
        if (videoView.isPlaying()) {
            videoView.seekTo(0);
            videoView.pause();
            statusTextView.setText("Stopped");
            stopButton.setEnabled(false); // Dezactivăm butonul de stop
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (videoView.isPlaying()) {
            currentPosition = videoView.getCurrentPosition();
            outState.putInt("currentPos", currentPosition);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt("currentPos");
            if (videoView != null) {
                videoView.seekTo(currentPosition);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentPosition > 0) {
            videoView.seekTo(currentPosition);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoView.stopPlayback();
    }
}
