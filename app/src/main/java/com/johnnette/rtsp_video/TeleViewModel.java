package com.johnnette.rtsp_video;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TeleViewModel extends ViewModel {

    public static TeleViewModel _Instance;
    private final MutableLiveData<DataTelemetry> mutableLiveData =
            new MutableLiveData<>(new DataTelemetry());
    private DataTelemetry dataModel ;


    // Singleton instance
    public static TeleViewModel getInstance(){
        if(_Instance == null ){
            _Instance = new TeleViewModel();
            return _Instance;
        }
        return _Instance;
    }
    public LiveData<DataTelemetry> getTelemetryData(){
 /*       if (mutableLiveData == null) {
            mutableLiveData = new MutableLiveData<DataTelemetry>();*/

            if(dataModel == null)
                dataModel = new DataTelemetry();

            mutableLiveData.setValue(dataModel);
        //}
        return mutableLiveData;
    }

    public void UpdateViewModel(DataTelemetry model){

        //Log.d("ViewModel", "UpdateViewModel:"+model);
        if((mutableLiveData != null) || (dataModel != null)){

            if (model != null) {
                this.dataModel = model ;

                Log.d("ViewModel", "Data: "+ mutableLiveData.getValue().getGroundSpeed());
            }

        }

        dataModel = model ;
        mutableLiveData.postValue(model);

    }

}
