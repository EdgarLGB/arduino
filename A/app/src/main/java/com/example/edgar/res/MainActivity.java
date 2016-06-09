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
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;
    private Button button5;
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



        button1 = (Button) findViewById(R.id.button);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);
        button5 = (Button) findViewById(R.id.button5);

        player1 = MediaPlayer.create(this, R.raw.son1);
        player2 = MediaPlayer.create(this, R.raw.son2);
        player3 = MediaPlayer.create(this, R.raw.son3);


        //add listener
        button1.setOnClickListener(new View.OnClickListener() {
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
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            //for start the music through a bluetooth speak
            @Override
            public void onClick(View v) {
                if (player1.isPlaying()) {
                    //if music is not playing, start it
                    player1.stop();
                    player1.reset();
                } else {
                    player1.prepareAsync();
                }
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start the thread for socket
                Executors.newSingleThreadExecutor().submit(new ArduinoThread());
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            //for start the music through a bluetooth speak
            @Override
            public void onClick(View v) {
                if (player2.isPlaying()) {
                    //if music is not playing, start it
                    player2.stop();
                    player2.reset();
                } else {
                    player2.start();
                }
            }
        });
        button5.setOnClickListener(new View.OnClickListener() {
            //for start the music through a bluetooth speak
            @Override
            public void onClick(View v) {
                if (player3.isPlaying()) {
                    //if music is not playing, start it
                    player3.stop();
                    player3.reset();
                } else {
                    player3.start();
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
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
