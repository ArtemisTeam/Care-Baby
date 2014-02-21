package com.example.care_babby;

import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Process;
import android.os.Vibrator;
import android.util.Log;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.BDNotifyListener;
import com.baidu.location.GeofenceClient;
import com.baidu.location.LocationClient;

public class Location extends Application {

	public LocationClient mLocationClient = null;
	public GeofenceClient mGeofenceClient;
	private String mData;  
	public MyLocationListenner myListener = new MyLocationListenner();
	public TextView mTv;
	public NotifyLister mNotifyer=null;
	public Vibrator mVibrator01;
	public static String TAG = "location_sender";
	public String username=null;
	
	@Override
	public void onCreate() {
		mLocationClient = new LocationClient( this );
		AVOSCloud.useAVCloudCN();
		AVOSCloud.initialize(this, "9f9kc7n2gfm4ug07arijhef5roy4kvq4l1xh6voy714hfswm", "2nuzsw9x0tw3mheybe7spewu43bfwnfoujry68g51973watr");
		/**——————————————————————————————————————————————————————————————————
		 * 这里的AK和应用签名包名绑定，如果使用在自己的工程中需要替换为自己申请的Key
		 * ——————————————————————————————————————————————————————————————————
		 */
		mLocationClient.setAK("8GX4jfnPmYXXeTwXP3QcccAy");
		mLocationClient.registerLocationListener( myListener );
		//mGeofenceClient = new GeofenceClient(this);

		super.onCreate(); 
		Log.d(TAG, "... Application onCreate... pid=" + Process.myPid());
	}
	
	/**
	 * 显示请求字符串
	 * @param str
	 */
	public void logMsg(String str) {
		try {
			mData = str;
			if ( mTv != null )
				mTv.setText(mData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 监听函数，有更新位置的时候，格式化成字符串，输出到屏幕中
	 */
	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return ;
			//获取电量百分比
			Intent batteryIntent = getApplicationContext().registerReceiver(null,
					new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
			int currLevel = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
			int total = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, 1);
			int percent = currLevel * 100 / total;
			
			Log.i("battery", "battery: " + percent + "%");
			AVObject loc = new AVObject("Location");
			loc.put("username", username+"_child");
			loc.put("Latitude",location.getLatitude());
			Log.i("latitude", location.getLatitude()+"");
			loc.put("Longtitude", location.getLongitude());
			Log.i("Longtitude",location.getLongitude()+"");
			loc.put("battery", percent+"");
			loc.saveInBackground(new SaveCallback() {

				@Override
				public void done(AVException e) {
					// TODO Auto-generated method stub
					if (e == null) {
						Log.i("state", "save success");
					} else {
						Log.i("state", "save fail");
					}
				}
			});
			
		}
		
		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null){
				return ; 
			}
		}
		
	}
	
	public class NotifyLister extends BDNotifyListener{
		public void onNotify(BDLocation mlocation, float distance){
			mVibrator01.vibrate(1000);
		}
	}
}