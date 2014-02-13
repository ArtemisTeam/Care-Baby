package com.example.baidumap;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class MainActivity extends Activity{
	BMapManager mBMapMan = null;
	MapView mMapView = null;
	LocationData locData=null;
	MyLocationOverlay myLocationOverlay=null;
	String username="211";
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		AVOSCloud.useAVCloudCN();
		AVOSCloud.initialize(this, "9f9kc7n2gfm4ug07arijhef5roy4kvq4l1xh6voy714hfswm", "2nuzsw9x0tw3mheybe7spewu43bfwnfoujry68g51973watr");
		AVAnalytics.trackAppOpened(getIntent());
		mBMapMan=new BMapManager(getApplication());
		mBMapMan.init("8GX4jfnPmYXXeTwXP3QcccAy", null);  
		//ע�⣺��������setContentViewǰ��ʼ��BMapManager���󣬷���ᱨ��
		setContentView(R.layout.activity_main);

		mMapView=(MapView)findViewById(R.id.bmapsView);

		myLocationOverlay = new MyLocationOverlay(mMapView);
		locData = new LocationData();
		//�ֶ���λ��Դ��Ϊ�찲�ţ���ʵ��Ӧ���У���ʹ�ðٶȶ�λSDK��ȡλ����Ϣ��Ҫ��SDK����ʾһ��λ�ã���Ҫʹ�ðٶȾ�γ�����꣨bd09ll��
		locData.latitude = 39.945;
		locData.longitude = 116.404;
		//locData.direction = 2.0f;
		myLocationOverlay.setData(locData);
		mMapView.getOverlays().add(myLocationOverlay);
		mMapView.refresh();
		mMapView.setSatellite(true);  
		mMapView.getController().setZoom(19);
		mMapView.getController().animateTo(new GeoPoint((int)(locData.latitude*1e6),
				(int)(locData.longitude* 1e6)));
		handler.postDelayed(runnable,5000); 
		////        	mMapView.setBuiltInZoomControls(true);
		//        	mMapView.setSatellite(true);  
		//        	//�����������õ����ſؼ�
		//        	MapController mMapController=mMapView.getController();
		//        	// �õ�mMapView�Ŀ���Ȩ,�����������ƺ�����ƽ�ƺ�����
		//        	GeoPoint point =new GeoPoint((int)(39.915* 1E6),(int)(116.404* 1E6));
		//        	//�ø����ľ�γ�ȹ���һ��GeoPoint����λ��΢�� (�� * 1E6)
		//        	mMapController.setCenter(point);//���õ�ͼ���ĵ�
		//        	mMapController.setZoom(19);//���õ�ͼzoom����[3,19]
	}
	private Handler handler = new Handler();
	private Runnable runnable = new Runnable() {
		public void run () {
			AVQuery<AVObject> query = new AVQuery<AVObject>("Location");
			query.whereEqualTo("username", "211_child");
			query.orderByDescending("createdAt");
			query.findInBackground(new FindCallback<AVObject>() {

				@Override
				public void done(List<AVObject> arg0, AVException arg1) {
					// TODO Auto-generated method stub
					if(arg1==null){
						if(arg0!=null){
						Log.i("state", arg0.size()+"");
						AVObject point;
						point=arg0.get(0);
						locData.latitude=point.getDouble("Latitude");
						locData.longitude=point.getDouble("Longtitude");
						Log.i("loc", "latitude "+locData.latitude);
						Log.i("loc", "longtitude "+locData.longitude);
						myLocationOverlay.setData(locData);
						mMapView.refresh();
						mMapView.getController().animateTo(new GeoPoint((int)(locData.latitude*1e6),
								(int)(locData.longitude* 1e6)));
						Log.i("state", "reanimate");
						}else{
							Log.i("state","query for nothing");
						}
					}else{
						Log.i("exception", arg1.getMessage());
					}
				}

			});
			handler.postDelayed(this,5000); 
		}

	};
	@Override
	protected void onDestroy(){
		mMapView.destroy();
		if(mBMapMan!=null){
			mBMapMan.destroy();
			mBMapMan=null;
		}
		handler.removeCallbacks(runnable); //ֹͣˢ��
		super.onDestroy();
	}
	@Override
	protected void onPause(){
		mMapView.onPause();
		if(mBMapMan!=null){
			mBMapMan.stop();
		}
		super.onPause();
	}
	@Override
	protected void onResume(){
		mMapView.onResume();
		if(mBMapMan!=null){
			mBMapMan.start();
		}
		super.onResume();
	}

} 
