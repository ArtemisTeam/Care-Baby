package com.example.baidumap;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKMapTouchListener;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.Symbol;
import com.baidu.mapapi.map.Symbol.Color;
import com.baidu.mapapi.map.Symbol.Stroke;
import com.baidu.mapapi.search.PlaceCaterActivity;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class MainActivity extends Activity implements OnClickListener{
	BMapManager mBMapMan = null;
	MapView mMapView = null;
	Button current_loc=null;
	Button trajectory=null;
	Button set_range=null;
	EditText edt=null;
	LocationData locData=null;
	MyLocationOverlay myLocationOverlay=null;
	String username="211";
	int set_distance;
	int tar_la;
	int tar_lo;
	boolean isFollowTrail=false;
	boolean isShowTrail=false;
	boolean isSetRange=false;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		AVOSCloud.useAVCloudCN();
		AVOSCloud.initialize(this, "9f9kc7n2gfm4ug07arijhef5roy4kvq4l1xh6voy714hfswm", "2nuzsw9x0tw3mheybe7spewu43bfwnfoujry68g51973watr");
		AVAnalytics.trackAppOpened(getIntent());
		mBMapMan=new BMapManager(getApplication());
		mBMapMan.init("8GX4jfnPmYXXeTwXP3QcccAy", null);  

		//注意：请在试用setContentView前初始化BMapManager对象，否则会报错
		setContentView(R.layout.activity_main);
		initView();
		initData();
	}

	public void initView()
	{
		mMapView=(MapView)findViewById(R.id.bmapsView);
		current_loc=(Button)findViewById(R.id.button1);
		current_loc.setOnClickListener(this);
		trajectory=(Button)findViewById(R.id.button2);
		trajectory.setOnClickListener(this);
		set_range=(Button)findViewById(R.id.button3);
		set_range.setOnClickListener(this);
		edt=(EditText)findViewById(R.id.editText1);
	}
	public void initData()
	{
		MKMapTouchListener mapTouchListener = new MKMapTouchListener(){
			@Override
			public void onMapClick(GeoPoint point) {
				//在此处理地图单击事件
			}

			@Override
			public void onMapDoubleClick(GeoPoint point) {
				//在此处理地图双击事件
			}

			@Override
			public void onMapLongClick(GeoPoint point) {
				//在此处理地图长按事件 
				tar_la=point.getLatitudeE6();
				tar_lo=point.getLongitudeE6();
			}
		};
		myLocationOverlay = new MyLocationOverlay(mMapView);
		locData = new LocationData();
		//手动将位置源置为天安门，在实际应用中，请使用百度定位SDK获取位置信息，要在SDK中显示一个位置，需要使用百度经纬度坐标（bd09ll）
		//		locData.latitude = 39.945;
		//		locData.longitude = 116.404;
		//locData.direction = 2.0f;
		//		myLocationOverlay.setData(locData);
		mMapView.getOverlays().add(myLocationOverlay);
		mMapView.refresh();
		mMapView.setSatellite(true);  
		mMapView.getController().setZoom(19);
		//		GeoPoint p1=new GeoPoint((int)(locData.latitude*1e6),
		//				(int)(locData.longitude* 1e6));
		//		mMapView.getController().animateTo(p1);
		//		handler.postDelayed(runnable,5000); 

		////        	mMapView.setBuiltInZoomControls(true);
		//        	mMapView.setSatellite(true);  
		//        	//设置启用内置的缩放控件
		//        	MapController mMapController=mMapView.getController();
		//        	// 得到mMapView的控制权,可以用它控制和驱动平移和缩放
		//        	GeoPoint point =new GeoPoint((int)(39.915* 1E6),(int)(116.404* 1E6));
		//        	//用给定的经纬度构造一个GeoPoint，单位是微度 (度 * 1E6)
		//        	mMapController.setCenter(point);//设置地图中心点
		//        	mMapController.setZoom(19);//设置地图zoom级别[3,19]
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
							if(locData.latitude==point.getDouble("Latitude")
									&&locData.longitude==point.getDouble("Longtitude")){
								return;
							}
							locData.latitude=point.getDouble("Latitude");
							locData.longitude=point.getDouble("Longtitude");
							Log.i("loc", "latitude "+locData.latitude);
							Log.i("loc", "longtitude "+locData.longitude);
							if(isFollowTrail==true){
								myLocationOverlay.setData(locData);
								mMapView.refresh();
								GeoPoint p1=new GeoPoint((int)(locData.latitude*1e6),
										(int)(locData.longitude* 1e6));
								mMapView.getController().animateTo(p1);
								Log.i("state", "reanimate");
							}else if(isSetRange==true){
								GeoPoint p1=new GeoPoint((int)(locData.latitude*1e6),
										(int)(locData.longitude* 1e6));
								GeoPoint p2=new GeoPoint(tar_la, tar_lo);
								double distance = DistanceUtil.getDistance(p1, p2);
								if(distance>set_distance){
									Toast.makeText(getApplicationContext(), "孩子超出安全范围！",
											Toast.LENGTH_SHORT).show();
								}
							}
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
		handler.removeCallbacks(runnable); //停止刷新
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.button1:
			FollowTrail();
			break;
		case R.id.button2:
			ShowTrail();
			break;
		case R.id.button3:
			SetRange();
		}
	}

	public void FollowTrail(){
		if(isFollowTrail==false){
			isFollowTrail=true;
		}else{
			isFollowTrail=false;
		}
	}

	public void ShowTrail(){

	}

	public void SetRange(){
		if(isSetRange==false){
			isSetRange=true;	
			set_distance=Integer.parseInt(edt.getText().toString().trim());
		}else{
			isSetRange=false;
			edt.setText(null);
		}
		//画圈待添加
//		GeoPoint p=new GeoPoint(tar_la, tar_lo);
//		Geometry palaceGeometry = new Geometry();
//		palaceGeometry.setCircle(p, set_distance);
		
	}

} 
