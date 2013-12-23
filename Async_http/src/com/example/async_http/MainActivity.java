package com.example.async_http;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final int MSG= 1;
	private Thread mThread;  
	TextView tv;
	 private Handler mHandler = new Handler() {  
	        public void handleMessage (Message msg) {//此方法在ui线程运行  
	            switch(msg.what) {  
	            case MSG:  
	            	tv.setText((CharSequence) msg.obj);
	            	Toast.makeText(getApplication(), (CharSequence) msg.obj, Toast.LENGTH_LONG).show();  
	                break;  
	            }  
	        }  
	    };  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tv=(TextView)findViewById(R.id.textView1);
		 mThread = new Thread(runnable);  
         mThread.start();//线程启动  
	}
	 Runnable runnable = new Runnable() {  
         
	        @Override  
	        public void run() {//run()在新的线程中运行  
	        	String message=null;
	            HttpClient hc = new DefaultHttpClient();  
	            HttpPost post=new HttpPost("http://carebaby.duapp.com/system/receivemessage.php");
				List<NameValuePair>params=new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", "卧槽"));
                params.add(new BasicNameValuePair("x", "终于"));
                params.add(new BasicNameValuePair("y", "存进去了"));
                try {  
                	post.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
                	HttpResponse hr = hc.execute(post);     
                	message=hr.getStatusLine().toString();
                    //Toast.makeText(getApplicationContext(), hr.getEntity().getContent().toString(),
                    //	     Toast.LENGTH_LONG).show();
                }
                catch(Exception ex){
                  	Log.i("http", "1"+ex.getMessage());
                }
                mHandler.obtainMessage(MSG,message).sendToTarget();
	        }	
	 };
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
