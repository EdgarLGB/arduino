package com.example.edgar.res;

import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.edgar.a.R;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private Button button_son1;
    private Button button_son2;
    private Button button_son3;
    private Button button_fan;
    private Socket socket;
    private PrintWriter writer;
    private static final String SERVER_IP = "192.168.4.1";
    private static final int PORT = 80;

    private int counter = 0;
    private CountDownTimer timer;
    private static final int PERIOD = 2;

    private MediaPlayer player1;
    private MediaPlayer player2;
    private MediaPlayer player3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //start the socket between arduino
        Executors.newSingleThreadExecutor().submit(new ArduinoThread());

        button_fan = (Button) findViewById(R.id.button_fan);
        button_son1 = (Button) findViewById(R.id.button_son1);
        button_son2 = (Button) findViewById(R.id.button_son2);
        button_son3 = (Button) findViewById(R.id.button_son3);

        player1 = MediaPlayer.create(this, R.raw.son1);
        player2 = MediaPlayer.create(this, R.raw.son2);
        player3 = MediaPlayer.create(this, R.raw.son3);


        //add listener
        button_fan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (counter == 0) {
                    //start the chrono
                    timer = new CountDownTimer(PERIOD * 1000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {
                            if (writer == null) {
                                Log.d("info", "null");
                                return;
                            }
                            writer.println(counter);
                            Log.d("info", "fan " + counter);
                            //reset the counter to 0
                            counter = 0;
                        }
                    };
                    timer.start();
                }
                counter++;
            }
        });
        player1.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(final MediaPlayer mp) {
                button_son1.setOnClickListener(new View.OnClickListener() {
                    //for start the music through a bluetooth speak
                    @Override
                    public void onClick(View v) {
                        if (mp.isPlaying()) {
                            //if music is not playing, start it
                            mp.pause();
                            mp.seekTo(0);
                        } else {
                            mp.start();
                        }                    }
                });
            }
        });
        player2.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(final MediaPlayer mp) {
                button_son2.setOnClickListener(new View.OnClickListener() {
                    //for start the music through a bluetooth speak
                    @Override
                    public void onClick(View v) {
                        if (mp.isPlaying()) {
                            //if music is not playing, start it
                            mp.pause();
                            mp.seekTo(0);
                        } else {
                            mp.start();
                        }                    }
                });
            }
        });
        player3.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(final MediaPlayer mp) {
                button_son3
                        .setOnClickListener(new View.OnClickListener() {
                    //for start the music through a bluetooth speak
                    @Override
                    public void onClick(View v) {
                        if (mp.isPlaying()) {
                            //if music is not playing, start it
                            mp.pause();
                            mp.seekTo(0);
                        } else {
                            mp.start();
                        }                    }
                });
            }
        });
//        button_son1.setOnClickListener(new View.OnClickListener() {
//            //for start the music through a bluetooth speak
//            @Override
//            public void onClick(View v) {
//                if (player1.isPlaying()) {
//                    //if music is not playing, start it
//                    player1.pause();
//                    player1.seekTo(0);
//                } else {
//                    player1.prepareAsync();
//                }
//            }
//        });
//        button_son2.setOnClickListener(new View.OnClickListener() {
//            //for start the music through a bluetooth speak
//            @Override
//            public void onClick(View v) {
//                if (player2.isPlaying()) {
//                    //if music is not playing, start it
//                    player2.pause();
//                    player2.seekTo(0);
//                } else {
//                    player2.prepareAsync();
//                }
//            }
//        });
//        button_son3.setOnClickListener(new View.OnClickListener() {
//            //for start the music through a bluetooth speak
//            @Override
//            public void onClick(View v) {
//                if (player3.isPlaying()) {
//                    //if music is not playing, start it
//                    player3.pause();
//                    player3.seekTo(0);
//                } else {
//                    player3.prepareAsync();
//                }
//            }
//        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        player1.release();
        player2.release();
        player3.release();
        if (writer != null)
            writer.close();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class ArduinoThread implements Runnable {

        @Override
        public void run() {
            try {
                InetAddress address = InetAddress.getByName(SERVER_IP);
                socket = new Socket(address, PORT);
                try {
                    writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
