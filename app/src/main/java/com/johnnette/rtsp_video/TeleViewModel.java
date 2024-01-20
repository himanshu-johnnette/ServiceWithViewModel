package com.johnnette.rtsp_video;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TeleViewModel extends ViewModel {
    MutableLiveData<DataTelemetry> mutableLiveData;

    public LiveData<DataTelemetry> getTelemetryData(){
        if (mutableLiveData == null) {
            mutableLiveData = new MutableLiveData<DataTelemetry>();

        }
        return mutableLiveData;
    }
}
