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
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class MainActivity extends Activity implements OnClickListener{
	BMapManager mBMapMan = null;
	MapView mMapView = null;
	Button current_loc=null;
	Button cancel_set=null;
	Button set_range=null;
	EditText edt=null;
	LocationData locData=null;
	MyLocationOverlay myLocationOverlay=null;
	GraphicsOverlay graphicsOverlay=null;
	String username="211";
	int set_distance;
	int tar_la;
	int tar_lo;
	boolean isAnimated=false;
	boolean isFollowTrail=false;
	boolean isSetRange=false;
	boolean isSame=false;
	private Handler handler = new Handler();

	private Runnable runnable = new Runnable() {
		@Override
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
								isSame=true;//本次获取的坐标与上一次相同
							}else{
								isSame=false;//本次获取的坐标与上一次获取的不同
								Log.i("loc", "latitude "+locData.latitude);
								Log.i("loc", "longtitude "+locData.longitude);
							}

							locData.latitude=point.getDouble("Latitude");
							locData.longitude=point.getDouble("Longtitude");
							String occur_time=point.getCreatedAt().toLocaleString();

							if(isFollowTrail==true){
								if(isSame==true
										&&isAnimated==true){
									return;
								}
								Log.i("state", "into isFollowTrail");
								Toast.makeText(getApplicationContext(), "出现时间: "+occur_time,
										Toast.LENGTH_SHORT).show();
								myLocationOverlay.setData(locData);
								//								mMapView.getOverlays().add(myLocationOverlay);  
								mMapView.refresh();
								GeoPoint p1=new GeoPoint((int)(locData.latitude*1e6),
										(int)(locData.longitude* 1e6));
								mMapView.getController().animateTo(p1);
								isAnimated=true;
								Log.i("state", "reanimate");
							}else if(isSetRange==true){
								Log.i("state", "into isSetRange");
								GeoPoint p1=new GeoPoint((int)(locData.latitude*1e6),
										(int)(locData.longitude* 1e6));
								GeoPoint p2=new GeoPoint(tar_la, tar_lo);
								double distance = DistanceUtil.getDistance(p1, p2);
								if(distance>set_distance){
									//孩子超出安全范围报警
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

	public void initData()
	{
		myLocationOverlay = new MyLocationOverlay(mMapView);
		locData = new LocationData();
		locData.latitude=999;
		locData.longitude=999;
		myLocationOverlay.setData(locData);
		mMapView.getOverlays().add(myLocationOverlay);
		mMapView.refresh();
		mMapView.setSatellite(true);  
		mMapView.getController().setZoom(19);

		handler.postDelayed(runnable,5000); 
	}
	public void initView()
	{
		mMapView=(MapView)findViewById(R.id.bmapsView);
		current_loc=(Button)findViewById(R.id.button1);
		current_loc.setOnClickListener(this);
		cancel_set=(Button)findViewById(R.id.button2);
		cancel_set.setOnClickListener(this);
		set_range=(Button)findViewById(R.id.button3);
		set_range.setOnClickListener(this);
		edt=(EditText)findViewById(R.id.editText1);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.button1://显示孩子当前位置
			FollowTrail();
			break;
		case R.id.button2://取消设定安全范围
			RemoveRange();
			break;
		case R.id.button3://设定安全范围，按这个之前需要输入半径
			SetRange();
			break;
		}
	}

	public void FollowTrail(){
		if(isFollowTrail==false){
			Log.i("state", "clicked FollowTrail");
			isSame=false;
			Toast.makeText(getApplicationContext(), "正在追踪孩子，请稍等。",
					Toast.LENGTH_SHORT).show();
			isFollowTrail=true;
		}else{
			Log.i("state", "clicked FollowTrail");
			isFollowTrail=false;
		}
	}
	public void SetRange(){
		Log.i("state", "clicked SetRange");
		String input=edt.getText().toString();
		if(Integer.parseInt(input)<=0){
			Toast.makeText(getApplicationContext(), "请输入合法范围",
					Toast.LENGTH_SHORT).show();
			return;
		}
		if(isSetRange==false){
			isSetRange=true;	
			set_distance=Integer.parseInt(input);
			Toast.makeText(getApplicationContext(), "已设定安全范围，半径为："+set_distance+"米",
					Toast.LENGTH_SHORT).show();
			tar_la=(int)(locData.latitude*1e6);
			tar_lo=(int)(locData.longitude*1e6);
		}else {
			if(set_distance!=Integer.parseInt(input)){
				isSetRange=true;	
				set_distance=Integer.parseInt(input);
				Toast.makeText(getApplicationContext(), "已设定安全范围，半径为："+set_distance+"米",
						Toast.LENGTH_SHORT).show();
			}else{
				isSetRange=false;
				edt.setText(null);
			}
		}
	}

	public void RemoveRange(){
		edt.setText(null);
		isSetRange=false;
		Toast.makeText(getApplicationContext(), "已取消设定安全范围",
				Toast.LENGTH_SHORT).show();
	}

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
} 
