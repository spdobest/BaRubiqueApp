package com.bestdealfinance.bdfpartner;

import android.util.Log;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
/**
 // Version 20: Added the Introduction Screen and Auto Login Feature.
 // Version 21: Removed the Wrong Link in the Know More Page.
 // Version 22: Removed the Bug which was directing App to go to Main Activity when User is not Logged in.
 // Version 25 (1.2.4): Sending it to Reliance
 // Version 26 (1.2.5): Will be sending to Relaince to Remove the Anamoly
 // Version 32: Sent to play store.
 // Version 33: Sent to play store. Compulsory Update Functionality Done USing Shouldupdateba API.
 // Version 30(1.2.5) Will be sending to Reliance.
 // Version 50(1.2.5) Will be Sending to PlayStore: This is the Version 2, Complete Revamp with Opportunity Functionality.
 //Version 60(2.0.0) Will be Sending to PlayStore: This is the Version 2, Complete Revamp with Opportunity Functionality.
 // Version 70 Send to PlayStore, fully featured APK as expected from Tech Team.

 */

/**
 * Created with Love by devesh on 17/6/15.
 */
public class Logs {
    public static int updatecode=3;
    public static boolean shouldLog = false;
    //public static String source="Reliance";
    public static String source="PlayStore";
    public static void LogD(String TAG, String MSG) {
        if (shouldLog) {
            if (MSG!=null) {
                Log.d(TAG, MSG);

            }
        }
    }

    public int findMaxRevenue(int[] a, long k)
    {
        int[] perBooth =a;
        Arrays.sort(perBooth);

        int i = perBooth.length-1;
        int revenue = 0;
        while(i >= 0 && k != 0)
        {
            int nxtBooth = 0;
            int currBooth = perBooth[i];
            if(i == 0)
            {
                nxtBooth = 0;
            }
            else
            {
                nxtBooth = perBooth[i-1];
            }

            int diff = currBooth - nxtBooth;
            while(diff != 0 && k != 0)
            {
                revenue += currBooth;
                currBooth--;
                diff--;
                k--;
            }
            i--;
        }
        return revenue;
    }
}
