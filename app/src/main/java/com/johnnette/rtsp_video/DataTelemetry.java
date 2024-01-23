package com.johnnette.rtsp_video;

import android.location.Location;
import android.provider.ContactsContract;

public class DataTelemetry {

    private float altitude ;
    private float groundSpeed ;
    private Location lat_long ;


    public DataTelemetry(){
        altitude    = 00;
        groundSpeed = 00;
        lat_long    =  new Location("SHESH");
    }

    public float getAltitude() {
        return altitude;
    }

    public void setAltitude(float altitude) {
        this.altitude = altitude;
    }

    public float getGroundSpeed() {
        return groundSpeed;
    }

    public void setGroundSpeed(float groundSpeed) {
        this.groundSpeed = groundSpeed;
    }

    public Location getLat_long() {
        return lat_long;
    }

    public void setLat_long(float lat, float lon) {
        lat_long.setLatitude(lat);
        lat_long.setLatitude(lon);
    }
}
