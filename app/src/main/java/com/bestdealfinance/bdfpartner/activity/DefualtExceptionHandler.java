package com.bestdealfinance.bdfpartner.activity;

import android.util.Log;


/**
 * Created by vikas on 10/6/16.
 */
public class DefualtExceptionHandler implements Thread.UncaughtExceptionHandler{

        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            Log.e("Crashed", "Received exception '" + ex.getMessage() + "' from thread " + thread.getName(), ex);
            thread.stop();
        }

}

