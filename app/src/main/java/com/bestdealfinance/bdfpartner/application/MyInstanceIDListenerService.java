package com.bestdealfinance.bdfpartner.application;

import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.iid.InstanceIDListenerService;

import java.io.IOException;

/**
 * Created by Harshit on 14-Jun-16.
 */
public class MyInstanceIDListenerService extends InstanceIDListenerService {

    private static final String TAG = "MyInstanceIDLS";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        new Thread(new Runnable() {
            @Override
            public void run() {
                InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
                String token = null;
                try {
                    token = instanceID.getToken(Constant.SENDER_ID,
                            GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    // [END get_token]
                    Log.i("GCM", "GCM Registration Token: " + token);



                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    // [END refresh_token]
}
