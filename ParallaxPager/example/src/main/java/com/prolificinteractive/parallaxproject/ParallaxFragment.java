package com.prolificinteractive.parallaxproject;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import com.prolificinteractive.parallaxpager.ParallaxContainer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;

public class ParallaxFragment extends Fragment implements ViewPager.OnPageChangeListener, View.OnClickListener {

  private MediaPlayer player;
  private Socket socket;
  private PrintWriter writer;
  private static final String SERVER_IP = "192.168.4.1";
  private static final int PORT = 80;
  private int counter = 0;
  private CountDownTimer timer;
  private static final int PERIOD = 2;
  private static final String FAN = "fan";
  private static final String SON = "son";

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

/*    //start the socket between arduino
    Executors.newSingleThreadExecutor().submit(new ArduinoThread());*/

    View view = inflater.inflate(R.layout.fragment_parallax, container, false);

    // find the parallax container
    ParallaxContainer parallaxContainer =
        (ParallaxContainer) view.findViewById(R.id.parallax_container);

    // specify whether pager will loop
    parallaxContainer.setLooping(true);

    // wrap the inflater and inflate children with custom attributes
    parallaxContainer.setupChildren(inflater,
        R.layout.parallax_view_1,
        R.layout.parallax_view_2,
        R.layout.parallax_view_3);

    parallaxContainer.findViewById(R.id.button_fan1).setOnClickListener(this);
    parallaxContainer.findViewById(R.id.button_fan2).setOnClickListener(this);
    parallaxContainer.findViewById(R.id.button_fan3).setOnClickListener(this);

    parallaxContainer.findViewById(R.id.button_son1).setOnClickListener(this);
    parallaxContainer.findViewById(R.id.button_son2).setOnClickListener(this);
    parallaxContainer.findViewById(R.id.button_son3).setOnClickListener(this);

    // optionally set a ViewPager.OnPageChangeListener
    parallaxContainer.setOnPageChangeListener(this);

    // initiate the mediaplayer with the first son
    player = MediaPlayer.create(getContext(), R.raw.son1);

    return view;
  }

  @Override public void onPageScrolled(int position, float offset, int offsetPixels) {
//    // example of manually setting view visibility
//    if (position == 1 && offset > 0.2) {
//      // just before leaving the screen, Earth will disappear
//      mEarthImageView.setVisibility(View.INVISIBLE);
//    } else if (position == 0 || position == 1) {
//      mEarthImageView.setVisibility(View.VISIBLE);
//    } else {
//      mEarthImageView.setVisibility(View.GONE);
//    }

  }

  @Override public void onPageSelected(int position) {
    Toast.makeText(getContext(), "Location: " + position, Toast.LENGTH_SHORT).show();
    player.release();
    switch (position % 3) {
      case 0:
        player = MediaPlayer.create(getContext(), R.raw.son1);
        break;
      case 1:
        player = MediaPlayer.create(getContext(), R.raw.son2);
        break;
      case 2:
        player = MediaPlayer.create(getContext(), R.raw.son3);
        break;
    }
  }

  @Override public void onPageScrollStateChanged(int state) {

  }

  @Override
  public void onStart() {
    super.onStart();
    //start the socket between arduino
    Executors.newSingleThreadExecutor().submit(new ArduinoThread());
    Log.i("info", "start");
  }

  @Override
  public void onStop() {
    //stop all the fan
    writer.println(0);
    Toast.makeText(getContext(), "stop all the fan", Toast.LENGTH_SHORT).show();
    try {
      socket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    writer.close();
    Log.i("info", "close");
    super.onStop();
  }

  @Override
  public void onClick(View v) {
    Log.i("tag", "tag=" + v.getTag());
    switch ((String)v.getTag()) {
      case FAN:
        if (counter == 0) {
          //start the chrono
          timer = new CountDownTimer(PERIOD * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
              if (writer == null) {
                Toast.makeText(getContext(), "Fail to start the fan " + counter, Toast.LENGTH_SHORT).show();
                counter = 0;
                return;
              }
              if (counter > 0 && counter < 4) {
                writer.println(counter);
                Toast.makeText(getContext(), "start the fan " + counter, Toast.LENGTH_SHORT).show();
                Log.d("info", "fan " + counter + " start");
              }
              //reset the counter to 0
              counter = 0;
            }
          };
          timer.start();
        }
        counter++;
        break;
      case SON:
        if (player.isPlaying()) {
          player.pause();
          player.seekTo(0);
        } else {
          player.start();
        }
        break;
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
          if (socket.isConnected()) {
            Log.i("info", "connect");
            Toast.makeText(getContext(), "Connect to arduino successfully", Toast.LENGTH_SHORT).show();
          } else {
            Log.i("info", "fail to connect");
            Toast.makeText(getContext(), "Fail to connect to arduino", Toast.LENGTH_SHORT).show();
          }
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
