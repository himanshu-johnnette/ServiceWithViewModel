package com.johnnette.rtsp_video;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import io.dronefleet.mavlink.MavlinkConnection;
import io.dronefleet.mavlink.MavlinkMessage;
import io.dronefleet.mavlink.minimal.Heartbeat;

public class MavlinkService extends Service {

    private static int      port  = 14550;
    private static String   ipAddress = "192.168.31.140";
    public static  AtomicBoolean connectionState;
    private static MavlinkConnection mavlinkConnection;
    private static MavlinkMessage mavlinkMessage;

    private final IBinder  mBinder = new MavlinkServiceBinder();

    public MavlinkService() {
        connectionState = new AtomicBoolean(false);
    }

    public class MavlinkServiceBinder extends Binder {
        MavlinkService getService(){
            return MavlinkService.this ;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        try {
                TCP_MavlinkConnectionService();

                stopSelf();

                Log.d("CONNECTION", "STOPPED SERVICE");
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Log.i("CONNECTION", "Received start id " + startId + ": " + intent);
        Toast.makeText(this, "OnStartCommand", Toast.LENGTH_SHORT).show();
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    /// ###########################################################  ///
    //                    FUNCTIONS BELOW THIS                      //
    /// ###########################################################  ///


    public static boolean TCP_MavlinkConnectionService()
            throws InterruptedException {

        Log.d("CONNECTION", "IN TCP MAVLINK");


        // if connection already exist skip
        if (connectionState.get())
            connectionState.set(false);

        // Separate thread for mavlink connection Test on startPage
        Thread connectionThread = new Thread( () -> {

            if (mavlinkConnection == null) {
                Log.d("CONNECTION", "IN THREAD");

                try (Socket socket = new Socket(ipAddress, port)){

                    mavlinkConnection = MavlinkConnection.create(socket.getInputStream(), socket.getOutputStream());

                    int beats = 0;
                    Log.d("Connection", "START LOOP");
                    while ((mavlinkMessage = mavlinkConnection.next()) != null) {

                        if (mavlinkMessage.getPayload() instanceof Heartbeat) {

                            beats++;
                            if (beats > 3) {
                                connectionState.set(true);
                                break;
                            }
                            Log.d("Connection", "HEARTBEAT MESSAGE");
                        }
                    }

                    Log.i("Connection", "OUT OF LOOP");
                } catch (EOFException eof) {
                    Log.e("Connection", eof.getMessage());

                } catch (IOException e) {
                    Log.e("Connection", e.getMessage());
                }

            } else {
                try {
                    int beats = 0;
                    Log.i("Connection", "START LOOP");
                    while ((mavlinkMessage = mavlinkConnection.next()) != null) {

                        if (mavlinkMessage.getPayload() instanceof Heartbeat) {

                            beats++;
                            if (beats > 3) {
                                connectionState.set(true);
                                break;
                            }
                            Log.i("Connection", "HEARTBEAT MESSAGE");

                        }
                    }

                    Log.i("Connection", "OUT OF LOOP");
                } catch (IOException e) {
                    Log.e("Connection", e.getMessage());
                }
            }
        });

        connectionThread.start();
        connectionThread.join(5000);

        Log.i("Connection", "EXIT CASE");

        return connectionState.get();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mavlinkConnection = null;
    }
}