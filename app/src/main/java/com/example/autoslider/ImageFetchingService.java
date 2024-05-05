package com.example.autoslider;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.List;

public class ImageFetchingService extends Service {

    private final IBinder binder = new ImageServiceBinder();

    public class ImageServiceBinder extends Binder{
        public ImageFetchingService getService() {
            return ImageFetchingService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("tagger", "service bounded...");
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("tagger", "service started...");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d("tagger", "service stopped...");
        super.onDestroy();
    }

    public List<String> fetchImage(String url){
        return  new ImageParser().fetchUrl(url);
    }

}
