package com.example.care_baby_parent;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.PushService;
import com.example.text_map_fragment.R;

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
        @Override
		protected Long doInBackground(Integer... i) { 
                try {
                                Thread.sleep(1000);
                        } catch (InterruptedException e) {
                                e.printStackTrace();
                        }
            return (long)1; 
        } 

        @Override
		protected void onProgressUpdate(Integer... progress) { 
        } 

        @Override
		protected void onPostExecute(Long result) { 
                //开启第二个Activity
        	Intent intent = new Intent();
			
			intent.setClass(Repeater.this, yindao1.class);
			startActivity(intent);
			Repeater.this.finish();
        } 
    }
}