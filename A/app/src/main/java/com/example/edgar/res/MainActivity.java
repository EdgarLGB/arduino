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
    private Socket socket;
    private PrintWriter writer;
    private static final String SERVER_IP = "192.168.4.1";
    private static final int PORT = 80;

    private int counter = 0;
    private CountDownTimer timer;
    private static final int PERIOD = 2;

    private MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        button1 = (Button) findViewById(R.id.button);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);

        player = MediaPlayer.create(this, R.raw.romance);



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
        button2.setOnClickListener(new View.OnClickListener() {
            //for start the music through a bluetooth speak
            @Override
            public void onClick(View v) {
                if (player.isPlaying()) {
                    //if music is not playing, start it
                    player.stop();
                    player.reset();
                } else {
                    player.start();
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
