package location_sender;

import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Process;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

public class Mylocation extends Application{
	public LocationClient mLocationClient = null;
	public MyLocationListenner myListener = new MyLocationListenner();
	String username=null;//用户名
	@Override
	public void onCreate() {
		AVOSCloud.useAVCloudCN();
		AVOSCloud.initialize(this, "9f9kc7n2gfm4ug07arijhef5roy4kvq4l1xh6voy714hfswm", "2nuzsw9x0tw3mheybe7spewu43bfwnfoujry68g51973watr");
		mLocationClient = new LocationClient( this );
		mLocationClient.setAK("8GX4jfnPmYXXeTwXP3QcccAy");
		mLocationClient.registerLocationListener( myListener );
		LocationClientOption option = new LocationClientOption();
		option.setCoorType("bd09ll");
		option.setScanSpan(5);	
		mLocationClient.setLocOption(option);
		super.onCreate(); 
		mLocationClient.start();
		Log.i("Mylocation", "... Application onCreate... pid=" + Process.myPid());
	}
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

		@Override
		public void onReceivePoi(BDLocation arg0) {
			// TODO Auto-generated method stub

		}
	}
}
