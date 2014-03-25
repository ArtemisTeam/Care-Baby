package com.example.care_baby;

import com.example.care_babby.R;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

public class Repeater extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.repeater);
        new InitTask().execute(1);
    }
    
    private class InitTask extends AsyncTask<Integer, Integer, Long> { 
        protected Long doInBackground(Integer... i) { 
                try {
                                Thread.sleep(2000);
                        } catch (InterruptedException e) {
                                e.printStackTrace();
                        }
            return (long)1; 
        } 

        protected void onProgressUpdate(Integer... progress) { 
        } 

        protected void onPostExecute(Long result) { 
                //开启第二个Activity
        	Intent intent = new Intent();
			
			intent.setClass(Repeater.this, MainActivity.class);
			startActivity(intent);
			Repeater.this.finish();
        } 
    }
}