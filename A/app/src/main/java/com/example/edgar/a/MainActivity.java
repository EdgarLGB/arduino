package com.example.edgar.a;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button button1;
    private Button button2;
    private ArduinoThread thread;
    private OnMessageReceived receiveListner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button1 = (Button) findViewById(R.id.button);
        button2 = (Button) findViewById(R.id.button2);
        //add listener

        //start socket
        receiveListner = new OnMessageReceived() {
            @Override
            public void messageReceived(String message) {

            }
        };
        thread = new ArduinoThread(receiveListner);
        thread.sendMessage();
    }
}
