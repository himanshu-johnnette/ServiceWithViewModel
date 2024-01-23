package com.johnnette.rtsp_video;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.renderscript.ScriptGroup.Binding;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.johnnette.rtsp_video.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private MavlinkService mavlinkService;
    
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.

            mavlinkService = 
                    ((MavlinkService.MavlinkServiceBinder)service).getService();

            // Tell the user about this for our demo.
            Toast.makeText(getApplicationContext(), "SERVICE CONNECTED",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.

            mavlinkService = null;
            Toast.makeText(getApplicationContext(), "SERVICE DISCONNECTED",
                    Toast.LENGTH_SHORT).show();
        }
    };
    public MainActivity(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        TeleViewModel teleViewModel = new ViewModelProvider(this).get(TeleViewModel.class);

        teleViewModel.getTelemetryData().observe(this, mutableLiveData ->{
            Log.d("OBSERVER", "OBSERVER CHANGE=="+mutableLiveData.getGroundSpeed());

            binding.groundSpeed.setText( String.valueOf(mutableLiveData.getGroundSpeed()));
        } ) ;

        Button startButton  = findViewById(R.id.startButton);
        Button stopButton   = findViewById(R.id.stopButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(getApplicationContext(), MavlinkService.class));
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //stopService( new Intent(getApplicationContext(), MavlinkService.class));
                Log.d("ViewModel", "OBSERVER CHANGE=="+ teleViewModel.getTelemetryData().getValue().getGroundSpeed());
            }
        });
    }


}