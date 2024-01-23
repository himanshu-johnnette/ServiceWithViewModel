package com.johnnette.rtsp_video;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.provider.ContactsContract;
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
import io.dronefleet.mavlink.common.GlobalPositionInt;
import io.dronefleet.mavlink.common.VfrHud;
import io.dronefleet.mavlink.minimal.Heartbeat;

public class MavlinkService extends Service {

    // Binder Class for binding Service
    public class MavlinkServiceBinder extends Binder {
        MavlinkService getService() {
            return MavlinkService.this;
        }
    }

    // End of Class definition
    private static int port = 14550;
    private static String ipAddress = "192.168.31.140";
    public static AtomicBoolean connectionState;
    private static MavlinkConnection mavlinkConnection;
    private static MavlinkMessage mavlinkMessage;
    private final IBinder mBinder = new MavlinkServiceBinder();

    private DataTelemetry model;

    public MavlinkService() {
        connectionState = new AtomicBoolean(false);
        model = new DataTelemetry();
    }


    @Override
    public void onCreate() {
        super.onCreate();

        TCP_MavlinkConnectionService();

        //stopSelf();

        Log.d("CONNECTION", "SERVICE EXITED");

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


    public boolean TCP_MavlinkConnectionService() {

        Log.d("CONNECTION", "IN TCP MAVLINK");

        // if connection already exist skip
        if (connectionState.get())
            connectionState.set(false);

        // Separate thread for mavlink connection Test on startPage
        Thread connectionThread = new Thread(() -> {

            if (mavlinkConnection == null) {
                Log.d("CONNECTION", "IN THREAD");

                try {
                        Socket socket = new Socket(ipAddress, port);

                        mavlinkConnection = MavlinkConnection.create( socket.getInputStream(), socket.getOutputStream());

                        if(mavlinkConnection != null) {

                            Log.d("Connection", "START LOOP");

                            while ((mavlinkMessage = mavlinkConnection.next()) != null)
                            {

                                IdentifyMessage(mavlinkMessage);

                                Log.d("Connection", "IN LOOP ");

/*                        if (mavlinkMessage.getPayload() instanceof Heartbeat) {

                            beats++;
                            if (beats > 3) {
                                connectionState.set(true);
                                break;
                            }
                            Log.d("Connection", "HEARTBEAT MESSAGE");
                        }*/
                            }
                        }
                        else {
                            Log.d("CONNECTION", "FAILED MAVLINK CONNECTION");
                            stopSelf();
                            return;
                        }

                    stopSelf();
                    Log.i("Connection", "OUT OF LOOP");
                } catch (IOException e) {
                    Log.e("Connection", e.getMessage());
                }

            }

            else {
                try {
                    int beats = 0;
                    Log.i("Connection", "START LOOP");

                    while ((mavlinkMessage = mavlinkConnection.next()) != null) {

                        IdentifyMessage(mavlinkMessage);

                        Log.d("Connection", "IN LOOP ");
                    }

                    Log.i("Connection", "OUT OF LOOP");
                } catch (IOException e) {
                    Log.e("Connection", e.getMessage());
                }
            }
        });

        connectionThread.start();

        Log.i("Connection", "EXIT CASE");

        return connectionState.get();
    }

    public void IdentifyMessage(MavlinkMessage<?> message) {

        //Log.d("ViewModel", "CHECKING//");
        if (message.getPayload() instanceof GlobalPositionInt position) {
            model.setLat_long(position.lat(), position.lon());

            Log.d("ViewModel", "== POSITION");

        }

        // VFR_HUD ( #74 ) for air speed and ground speed
        else if (message.getPayload() instanceof VfrHud hud) {

            model.setGroundSpeed(hud.groundspeed());
            Log.d("ViewModel", "== GROUND SPEED");

        }

        //Log.d("VIEWMODEL", "IdentifyMessage: "+model);
        TeleViewModel.getInstance().UpdateViewModel(model);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}