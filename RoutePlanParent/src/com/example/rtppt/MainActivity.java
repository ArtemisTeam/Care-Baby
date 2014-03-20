package com.example.rtppt;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVPush;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.SendCallback;
import com.avos.avoscloud.SignUpCallback;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MKMapTouchListener;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.map.RouteOverlay;
import com.baidu.mapapi.map.TransitOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPlanNode;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKRoute;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.routeplan.R;

public class MainActivity extends Activity {
	//UI相关
	Button mBtnTransit = null;	// 公交搜索
	Button mBtnWalk = null;	// 步行搜索
	Button mSendRoute = null; //发送路线

	//浏览路线节点相关
	Button mBtnPre = null;//上一个节点
	Button mBtnNext = null;//下一个节点
	int nodeIndex = -2;//节点索引,供浏览节点时使用
	MKRoute route = null;//保存驾车/步行路线数据的变量，供浏览节点时使用
	TransitOverlay transitOverlay = null;//保存公交路线图层数据的变量，供浏览节点时使用
	RouteOverlay routeOverlay = null; 
	boolean useDefaultIcon = false;
	int searchType = -1;//记录搜索的类型，区分驾车/步行和公交
	private PopupOverlay   pop  = null;//弹出泡泡图层，浏览节点时使用
	private TextView  popupText = null;//泡泡view
	private View viewCache = null;

	//地图相关，使用继承MapView的MyRouteMapView目的是重写touch事件实现泡泡处理
	//如果不处理touch事件，则无需继承，直接使用MapView即可
	MapView mMapView = null;	// 地图View
	//搜索相关
	MKSearch mSearch = null;	// 搜索模块，也可去掉地图模块独立使用

	//网络相关
	private Thread mThread;
	private Handler handler = new Handler();
	//地图相关
	private double latitude;
	private double longtitude;
	private String targetcity="北京";
	private String startpoint=null;
	private String endpoint=null;
	private GeoPoint pt=null;
	private String username="wanghe";
	private String password="wanghe";
	private String InstallationId=null;
	private String targetID=null;
	int traffic;//walk=1 bus=0

	private Runnable runnableA = new Runnable() {
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

							latitude=point.getDouble("Latitude");
							longtitude=point.getDouble("Longtitude");

							pt=new GeoPoint((int)(latitude*1e6),
									(int)(longtitude* 1e6));
							mSearch.reverseGeocode(pt);
						}
					}
				}

			});
			handler.postDelayed(this,5000); 
		}

	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/**
		 * 使用地图sdk前需先初始化BMapManager.
		 * BMapManager是全局的，可为多个MapView共用，它需要地图模块创建前创建，
		 * 并在地图地图模块销毁后销毁，只要还有地图模块在使用，BMapManager就不应该销毁
		 */
		DemoApplication app = (DemoApplication)this.getApplication();
		if (app.mBMapManager == null) {
			app.mBMapManager = new BMapManager(getApplicationContext());
			/**
			 * 如果BMapManager没有初始化则初始化BMapManager
			 */
			app.mBMapManager.init(DemoApplication.strKey,new DemoApplication.MyGeneralListener());
		}
		setContentView(R.layout.routeplan);
		CharSequence titleLable="路线规划功能";
		setTitle(titleLable);

		//加载AVOS数据
		initData();
		//初始化地图
		mMapView = (MapView)findViewById(R.id.bmapView);
		mMapView.setBuiltInZoomControls(false);
		mMapView.getController().setZoom(12);
		mMapView.getController().enableClick(true);

		//初始化按键
		mBtnTransit = (Button)findViewById(R.id.transit);
		mBtnWalk = (Button)findViewById(R.id.walk);
		mBtnPre = (Button)findViewById(R.id.pre);
		mBtnNext = (Button)findViewById(R.id.next);
		mSendRoute=(Button)findViewById(R.id.send_route);
		mSendRoute.setVisibility(View.INVISIBLE);
		mBtnPre.setVisibility(View.INVISIBLE);
		mBtnNext.setVisibility(View.INVISIBLE);

		//按键点击事件
		OnClickListener clickListener = new OnClickListener(){
			@Override
			public void onClick(View v) {
				//发起搜索
				startpoint=((EditText)findViewById(R.id.start)).getText().toString();
				endpoint=((EditText)findViewById(R.id.end)).getText().toString();
				SearchButtonProcess(v);
			}
		};
		OnClickListener nodeClickListener = new OnClickListener(){
			@Override
			public void onClick(View v) {
				//浏览路线节点
				nodeClick(v);
			}
		};
		OnClickListener sendrouteClickListener = new OnClickListener(){
			@Override
			public void onClick(View v) {
				//发送路线
				AVQuery pushQuery = AVInstallation.getQuery();
				pushQuery.whereEqualTo("installationId", targetID);
				AVPush push = new AVPush();
				push.setQuery(pushQuery);
				JSONObject data=new JSONObject();
				data.put("action",  "com.avos.UPDATE_STATUS");
				data.put("send_time",getDate());
				data.put("msg",startpoint+"|"+endpoint+"|"+traffic);
				push.setData(data);
				push.sendInBackground(new SendCallback() {

					@Override
					public void done(AVException e) {
						// TODO Auto-generated method stub
						if (e == null) {
							Log.i("push", "success!");
						} else {
							Log.i("push", "failure");
						}
					}
				});
			}
		};


		mBtnTransit.setOnClickListener(clickListener); 
		mBtnWalk.setOnClickListener(clickListener);
		mBtnPre.setOnClickListener(nodeClickListener);
		mBtnNext.setOnClickListener(nodeClickListener);
		mSendRoute.setOnClickListener(sendrouteClickListener);
		//创建 弹出泡泡图层
		createPaopao();

		//地图点击事件处理
		mMapView.regMapTouchListner(new MKMapTouchListener(){

			@Override
			public void onMapClick(GeoPoint point) {
				//在此处理地图点击事件 
				//消隐pop
				if ( pop != null ){
					pop.hidePop();
				}
			}

			@Override
			public void onMapDoubleClick(GeoPoint point) {

			}

			@Override
			public void onMapLongClick(GeoPoint point) {

			}

		});
		// 初始化搜索模块，注册事件监听
		mSearch = new MKSearch();
		mSearch.init(app.mBMapManager, new MKSearchListener(){

			@Override
			public void onGetTransitRouteResult(MKTransitRouteResult res,
					int error) {
				//起点或终点有歧义，需要选择具体的城市列表或地址列表
				if (error == MKEvent.ERROR_ROUTE_ADDR){
					//遍历所有地址
					//						ArrayList<MKPoiInfo> stPois = res.getAddrResult().mStartPoiList;
					//						ArrayList<MKPoiInfo> enPois = res.getAddrResult().mEndPoiList;
					//						ArrayList<MKCityListInfo> stCities = res.getAddrResult().mStartCityList;
					//						ArrayList<MKCityListInfo> enCities = res.getAddrResult().mEndCityList;
					return;
				}
				if (error != 0 || res == null) {
					Toast.makeText(MainActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
					return;
				}

				searchType = 1;
				transitOverlay = new TransitOverlay (MainActivity.this, mMapView);
				// 此处仅展示一个方案作为示例
				transitOverlay.setData(res.getPlan(0));
				//清除其他图层
				mMapView.getOverlays().clear();
				//添加路线图层
				mMapView.getOverlays().add(transitOverlay);
				//执行刷新使生效
				mMapView.refresh();
				// 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
				mMapView.getController().zoomToSpan(transitOverlay.getLatSpanE6(), transitOverlay.getLonSpanE6());
				//移动地图到起点
				mMapView.getController().animateTo(res.getStart().pt);
				//重置路线节点索引，节点浏览时使用
				nodeIndex = 0;
				mBtnPre.setVisibility(View.VISIBLE);
				mBtnNext.setVisibility(View.VISIBLE);
				mSendRoute.setVisibility(View.VISIBLE);
			}

			@Override
			public void onGetWalkingRouteResult(MKWalkingRouteResult res,
					int error) {
				//起点或终点有歧义，需要选择具体的城市列表或地址列表
				if (error == MKEvent.ERROR_ROUTE_ADDR){
					//路线搜索起点或终点有歧义
					Toast.makeText(MainActivity.this, "抱歉，起点或终点有歧义，请重新输入", Toast.LENGTH_SHORT).show();
					return;
				}
				if(error == MKEvent.ERROR_RESULT_NOT_FOUND){
					//未找到搜索结果
					Toast.makeText(MainActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
					return;
				}
				// 错误号可参考MKEvent中的定义
				if (error != 0 || res == null) {
					Toast.makeText(MainActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
					return;
				}

				searchType = 2;
				routeOverlay = new RouteOverlay(MainActivity.this, mMapView);
				// 此处仅展示一个方案作为示例
				routeOverlay.setData(res.getPlan(0).getRoute(0));
				//清除其他图层
				mMapView.getOverlays().clear();
				//添加路线图层
				mMapView.getOverlays().add(routeOverlay);
				//执行刷新使生效
				mMapView.refresh();
				// 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
				mMapView.getController().zoomToSpan(routeOverlay.getLatSpanE6(), routeOverlay.getLonSpanE6());
				//移动地图到起点
				mMapView.getController().animateTo(res.getStart().pt);
				//将路线数据保存给全局变量
				route = res.getPlan(0).getRoute(0);
				//重置路线节点索引，节点浏览时使用
				nodeIndex = -1;
				mBtnPre.setVisibility(View.VISIBLE);
				mBtnNext.setVisibility(View.VISIBLE);
				mSendRoute.setVisibility(View.VISIBLE);
			}
			@Override
			public void onGetAddrResult(MKAddrInfo res, int error) {
				if(error==0){
					if(res.type==MKAddrInfo.MK_REVERSEGEOCODE){
						startpoint=res.strAddr;
						((EditText)findViewById(R.id.start)).setText(startpoint);
					}
				}
			}
			@Override
			public void onGetPoiResult(MKPoiResult res, int arg1, int arg2) {
			}
			@Override
			public void onGetBusDetailResult(MKBusLineResult result, int iError) {
			}

			@Override
			public void onGetSuggestionResult(MKSuggestionResult res, int arg1) {
			}

			@Override
			public void onGetPoiDetailSearchResult(int type, int iError) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onGetShareUrlResult(MKShareUrlResult result, int type,
					int error) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onGetDrivingRouteResult(MKDrivingRouteResult arg0,
					int arg1) {
				// TODO Auto-generated method stub

			}
		});
		mThread = new Thread(runnable);
		mThread.start();
	}
	//加载AVOS数据
	void initData(){
		AVOSCloud.useAVCloudCN();
		AVOSCloud.initialize(this, "9f9kc7n2gfm4ug07arijhef5roy4kvq4l1xh6voy714hfswm", "2nuzsw9x0tw3mheybe7spewu43bfwnfoujry68g51973watr");
		PushService.setDefaultPushCallback(this, MainActivity.class);
		InstallationId=AVInstallation.getCurrentInstallation().getInstallationId();
		Log.d("state","this device id is "+InstallationId);
		AVInstallation.getCurrentInstallation().saveInBackground();

		//更新_User表中的installationId
		AVUser user=new AVUser();
		AVUser.logInInBackground(username+"_parent", password, new LogInCallback<AVUser>() {

			@Override
			public void done(AVUser arg0, AVException arg1) {
				// TODO Auto-generated method stub
				if(arg1==null&&arg0!=null){
					Log.i("state","家长登陆成功");
					//					user.remove("installationid");
					arg0.put("installationid", InstallationId);
					Log.i("state","存储installationid "+InstallationId);
					arg0.signUpInBackground(new SignUpCallback() {

						@Override
						public void done(AVException arg0) {
							// TODO Auto-generated method stub
							if (arg0 == null) {
								Log.i("state","修改成功");

								//获取目标installationid
								AVQuery<AVObject> query2 = new AVQuery<AVObject>("_User");
								query2.whereEqualTo("username", username+"_child");
								query2.findInBackground(new FindCallback<AVObject>() {

									@Override
									public void done(List<AVObject> avObjects, AVException e) {
										if (e == null) {
											Log.i("成功222", "查询到" + avObjects.size() + " 条符合条件的数据");
											targetID=avObjects.get(0).getString("installationid");
											Log.i("state222", "targetID is "+targetID);
										} else {
											Log.i("失败222", "查询错误: " + e.getMessage());
										}
									}
								});
							} else {
								Log.i("state","修改失败"+arg0.getCode());
							}
						}
					});
				}else if(arg0==null){
					Log.i("state","用户名或者密码错误");
				}
			}
		});
		handler.postDelayed(runnableA,5000); 
	}

	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			HttpClient client = new DefaultHttpClient();
			StringBuilder builder = new StringBuilder();

			HttpGet myget = new HttpGet("http://api.go2map.com/engine/api/ipcity/json?parameters");
			try {
				HttpResponse response = client.execute(myget);
				BufferedReader reader = new BufferedReader(new InputStreamReader(
						response.getEntity().getContent()));
				for (String s = reader.readLine(); s != null; s = reader.readLine()) {
					builder.append(s);
				}
				JSONObject jsonObject =JSON.parseObject(builder.toString());//JSON.parseObject(builder.toString())
				targetcity = jsonObject.getString("city");
				Log.i("json", targetcity);
			} catch (Exception e) {
				Log.v("url response", "false");
				e.printStackTrace();
			}
		}
	};
	/**
	 * 发起路线规划搜索示例
	 * @param v
	 */
	void SearchButtonProcess(View v) {
		//重置浏览节点的路线数据
		route = null;
		routeOverlay = null;
		transitOverlay = null; 
		mBtnPre.setVisibility(View.INVISIBLE);
		mBtnNext.setVisibility(View.INVISIBLE);
		mSendRoute.setVisibility(View.INVISIBLE);
		// 处理搜索按钮响应
		EditText editSt = (EditText)findViewById(R.id.start);
		EditText editEn = (EditText)findViewById(R.id.end);

		// 对起点终点的name进行赋值，也可以直接对坐标赋值，赋值坐标则将根据坐标进行搜索
		MKPlanNode stNode = new MKPlanNode();
		stNode.pt=pt;//设置起点为孩子最新坐标
//		stNode.name = editSt.getText().toString();
		MKPlanNode enNode = new MKPlanNode();
		enNode.name = editEn.getText().toString();

		// 实际使用中请对起点终点城市进行正确的设定
		if (mBtnWalk.equals(v)) {
			traffic=1;
			mSearch.walkingSearch(targetcity, stNode, targetcity, enNode);
		} else if (mBtnTransit.equals(v)) {
			traffic=0;
			mSearch.transitSearch(targetcity, stNode, enNode);
		} 
	}
	/**
	 * 节点浏览示例
	 * @param v
	 */
	public void nodeClick(View v){
		viewCache = getLayoutInflater().inflate(R.layout.custom_text_view, null);
		popupText =(TextView) viewCache.findViewById(R.id.textcache);
		if (searchType == 0 || searchType == 2){
			//驾车、步行使用的数据结构相同，因此类型为驾车或步行，节点浏览方法相同
			if (nodeIndex < -1 || route == null || nodeIndex >= route.getNumSteps())
				return;

			//上一个节点
			if (mBtnPre.equals(v) && nodeIndex > 0){
				//索引减
				nodeIndex--;
				//移动到指定索引的坐标
				mMapView.getController().animateTo(route.getStep(nodeIndex).getPoint());
				//弹出泡泡
				popupText.setBackgroundResource(R.drawable.popup);
				popupText.setText(route.getStep(nodeIndex).getContent());
				pop.showPopup(BMapUtil.getBitmapFromView(popupText),
						route.getStep(nodeIndex).getPoint(),
						5);
			}
			//下一个节点
			if (mBtnNext.equals(v) && nodeIndex < (route.getNumSteps()-1)){
				//索引加
				nodeIndex++;
				//移动到指定索引的坐标
				mMapView.getController().animateTo(route.getStep(nodeIndex).getPoint());
				//弹出泡泡
				popupText.setBackgroundResource(R.drawable.popup);
				popupText.setText(route.getStep(nodeIndex).getContent());
				pop.showPopup(BMapUtil.getBitmapFromView(popupText),
						route.getStep(nodeIndex).getPoint(),
						5);
			}
		}
		if (searchType == 1){
			//公交换乘使用的数据结构与其他不同，因此单独处理节点浏览
			if (nodeIndex < -1 || transitOverlay == null || nodeIndex >= transitOverlay.getAllItem().size())
				return;

			//上一个节点
			if (mBtnPre.equals(v) && nodeIndex > 1){
				//索引减
				nodeIndex--;
				//移动到指定索引的坐标
				mMapView.getController().animateTo(transitOverlay.getItem(nodeIndex).getPoint());
				//弹出泡泡
				popupText.setBackgroundResource(R.drawable.popup);
				popupText.setText(transitOverlay.getItem(nodeIndex).getTitle());
				pop.showPopup(BMapUtil.getBitmapFromView(popupText),
						transitOverlay.getItem(nodeIndex).getPoint(),
						5);
			}
			//下一个节点
			if (mBtnNext.equals(v) && nodeIndex < (transitOverlay.getAllItem().size()-2)){
				//索引加
				nodeIndex++;
				//移动到指定索引的坐标
				mMapView.getController().animateTo(transitOverlay.getItem(nodeIndex).getPoint());
				//弹出泡泡
				popupText.setBackgroundResource(R.drawable.popup);
				popupText.setText(transitOverlay.getItem(nodeIndex).getTitle());
				pop.showPopup(BMapUtil.getBitmapFromView(popupText),
						transitOverlay.getItem(nodeIndex).getPoint(),
						5);
			}
		}

	}
	/**
	 * 创建弹出泡泡图层
	 */
	public void createPaopao(){

		//泡泡点击响应回调
		PopupClickListener popListener = new PopupClickListener(){
			@Override
			public void onClickedPopup(int index) {
				Log.v("click", "clickapoapo");
			}
		};
		pop = new PopupOverlay(mMapView,popListener);
	}
	/**
	 * 跳转自设路线Activity
	 */
	public void intentToActivity(){
		//跳转到自设路线演示demo
		//			Intent intent = new Intent(this, CustomRouteOverlayDemo.class);
		//	    	startActivity(intent); 
	}
	//返回时间
	private String getDate() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		return format.format(new Date());
	}
	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}
	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}
	@Override
	protected void onDestroy() {
		mMapView.destroy();
		mSearch.destory();
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mMapView.onRestoreInstanceState(savedInstanceState);
	}
}
