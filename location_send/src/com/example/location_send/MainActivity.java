package com.example.location_send;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MainActivity extends Activity {
	private LocationClient mLocClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		((Location)getApplication()).username = "211";
		mLocClient = ((Location)getApplication()).mLocationClient;
		mLocClient.setAK("8GX4jfnPmYXXeTwXP3QcccAy");
		setLocationOption();
		mLocClient.start();
		mLocClient.requestLocation();	
	}

	//������ز���
	private void setLocationOption(){
		LocationClientOption option = new LocationClientOption();

		option.setCoorType("bd09ll");		//������������
		option.setServiceName("com.baidu.location.service_v2.9");
		option.setScanSpan(5000);	//���ö�λģʽ��С��1����һ�ζ�λ;���ڵ���1����ʱ��λ
		option.setPriority( LocationClientOption.GpsFirst  );//����GPS����
		option.disableCache(true);		
		mLocClient.setLocOption(option);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onDestroy() {
		mLocClient.stop();
		((Location)getApplication()).mTv = null;
		super.onDestroy();
	}
	
}
