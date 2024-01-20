package com.johnnette.rtsp_video;

import android.provider.ContactsContract;

public class DataTelemetry {

    private String altitude ;
    private String groundSpeed ;
    private String lat_long ;


    public DataTelemetry(){
        altitude    = "00";
        groundSpeed = "00";
        lat_long    = "shesh";
    }
    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public String getGroundSpeed() {
        return groundSpeed;
    }

    public void setGroundSpeed(String groundSpeed) {
        this.groundSpeed = groundSpeed;
    }

    public String getLat_long() {
        return lat_long;
    }

    public void setLat_long(String lat_long) {
        this.lat_long = lat_long;
    }


}
