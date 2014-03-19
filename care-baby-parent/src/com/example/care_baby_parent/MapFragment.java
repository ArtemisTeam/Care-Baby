package com.example.care_baby_parent;

import java.util.List;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.LogUtil.log;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.text_map_fragment.R;

import android.os.Vibrator;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MapFragment extends Fragment {

    static MapView mMapView = null;
    private MapController mMapController = null;

    BMapManager mBMapMan = null;
	Button bt1=null;
	EditText edt=null;
	GraphicsOverlay graphicsOverlay=null;
	String username=null;
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
			query.whereEqualTo("username", username+"_child");
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
								Toast.makeText(getActivity().getApplicationContext(), "出现时间: "+occur_time,
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
									Vibrator vib = (Vibrator) getActivity().getSystemService(Service.VIBRATOR_SERVICE);

									vib.vibrate(1000);

									Toast.makeText(getActivity().getApplicationContext(), "孩子超出安全范围！"+distance,
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
    MyLocationOverlay myLocationOverlay = null;
    LocationData locData = null;

    static MapFragment newInstance() {
        MapFragment f = new MapFragment();
        return f;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AVOSCloud.useAVCloudCN();
		AVOSCloud.initialize(getActivity(), "9f9kc7n2gfm4ug07arijhef5roy4kvq4l1xh6voy714hfswm", "2nuzsw9x0tw3mheybe7spewu43bfwnfoujry68g51973watr");
		AVAnalytics.trackAppOpened(getActivity().getIntent());
		mBMapMan=new BMapManager(getActivity().getApplication());
		mBMapMan.init("8GX4jfnPmYXXeTwXP3QcccAy", null);  
		SharedPreferences preferences = getActivity().getSharedPreferences("userInfo",Context.MODE_PRIVATE);   
        username = preferences.getString("username", "");  
		log.i(username);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_locationoverlay, container, false);

        mMapView = (MapView) v.findViewById(R.id.bmapsView);
        mMapController = mMapView.getController();
        mMapView.getController().setZoom(14);
        mMapView.getController().enableClick(true);
        mMapView.setBuiltInZoomControls(true);

        initData();
        
        bt1=(Button)v.findViewById(R.id.button1);
		bt1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根
				final String[] items = new String[]{"当前孩子位置","设定安全范围","取消安全范围设定"};  
				new AlertDialog.Builder(getActivity()).setTitle("功能列表").setItems(items, new DialogInterface.OnClickListener() {  
				    @Override  
				    public void onClick(DialogInterface dialog, int which) {  
				        // TODO Auto-generated method stub  
				        switch (which) {  
				        case 0:  
				        	FollowTrail();
				            break;  
				        case 1:  
				        	String input=null;
							new AlertDialog.Builder(getActivity())  
							.setTitle("请输入距离半径")  
							.setIcon(android.R.drawable.ic_dialog_info)  
							.setView(edt=new EditText(getActivity()))  
							.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						        public void onClick(DialogInterface dialog, int whichButton) {
						            SetRange(edt.getText().toString());
						            }
						            })  
							.setNegativeButton("取消", null)  
							.show(); 
				            break;  
				        case 2: 
				        	RemoveRange();
				            break;  
				        }  
				    }  
				}).show();  
			}
		});
        return v;
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

   


    public void FollowTrail(){
		if(isFollowTrail==false){
			Log.i("state", "clicked FollowTrail");
			isSame=false;
			Toast.makeText(getActivity().getApplicationContext(), "正在追踪孩子，请稍等。",
					Toast.LENGTH_SHORT).show();
			isFollowTrail=true;
		}else{
			Log.i("state", "clicked FollowTrail");
			isFollowTrail=false;
		}
	}
	public void SetRange(String input){
		for(int i=0;i<input.length();i++)
		{
			if( !Character.isDigit(input.charAt(i)) )
				Toast.makeText(getActivity().getApplicationContext(), "请输入合法范围",
						Toast.LENGTH_SHORT).show();
					return;
			}
		//String input=edt.getText().toString();
		if(Integer.parseInt(input)<=0){
				Toast.makeText(getActivity().getApplicationContext(), "请输入合法范围",
						Toast.LENGTH_SHORT).show();
			return;
		}
		if(isSetRange==false){
			isSetRange=true;	
			set_distance=Integer.parseInt(input);
			Toast.makeText(getActivity().getApplicationContext(), "已设定安全范围，半径为："+set_distance+"米",
					Toast.LENGTH_SHORT).show();
			tar_la=(int)(locData.latitude*1e6);
			tar_lo=(int)(locData.longitude*1e6);
		}else {
			if(set_distance!=Integer.parseInt(input)){
				isSetRange=true;	
				set_distance=Integer.parseInt(input);
				Toast.makeText(getActivity().getApplicationContext(), "已设定安全范围，半径为："+set_distance+"米",
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
		Toast.makeText(getActivity().getApplicationContext(), "已取消设定安全范围",
				Toast.LENGTH_SHORT).show();
	}

    
    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        mMapView.destroy();
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);

    }

}
	

	
