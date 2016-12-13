package com.aaronps.wifeeper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class ServiceStarter extends Activity {

    private static final String TAG = "ServiceStarter";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate");

        Intent intent = new Intent(this, WifiKeeperService.class);
        startService(intent);
        finish();
    }
}
