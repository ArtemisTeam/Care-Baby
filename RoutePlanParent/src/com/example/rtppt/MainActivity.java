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
	//UI���
	Button mBtnTransit = null;	// ��������
	Button mBtnWalk = null;	// ��������
	Button mSendRoute = null; //����·��

	//���·�߽ڵ����
	Button mBtnPre = null;//��һ���ڵ�
	Button mBtnNext = null;//��һ���ڵ�
	int nodeIndex = -2;//�ڵ�����,������ڵ�ʱʹ��
	MKRoute route = null;//����ݳ�/����·�����ݵı�����������ڵ�ʱʹ��
	TransitOverlay transitOverlay = null;//���湫��·��ͼ�����ݵı�����������ڵ�ʱʹ��
	RouteOverlay routeOverlay = null; 
	boolean useDefaultIcon = false;
	int searchType = -1;//��¼���������ͣ����ּݳ�/���к͹���
	private PopupOverlay   pop  = null;//��������ͼ�㣬����ڵ�ʱʹ��
	private TextView  popupText = null;//����view
	private View viewCache = null;

	//��ͼ��أ�ʹ�ü̳�MapView��MyRouteMapViewĿ������дtouch�¼�ʵ�����ݴ���
	//���������touch�¼���������̳У�ֱ��ʹ��MapView����
	MapView mMapView = null;	// ��ͼView
	//�������
	MKSearch mSearch = null;	// ����ģ�飬Ҳ��ȥ����ͼģ�����ʹ��

	//�������
	private Thread mThread;
	private Handler handler = new Handler();
	//��ͼ���
	private double latitude;
	private double longtitude;
	private String targetcity="����";
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
		 * ʹ�õ�ͼsdkǰ���ȳ�ʼ��BMapManager.
		 * BMapManager��ȫ�ֵģ���Ϊ���MapView���ã�����Ҫ��ͼģ�鴴��ǰ������
		 * ���ڵ�ͼ��ͼģ�����ٺ����٣�ֻҪ���е�ͼģ����ʹ�ã�BMapManager�Ͳ�Ӧ������
		 */
		DemoApplication app = (DemoApplication)this.getApplication();
		if (app.mBMapManager == null) {
			app.mBMapManager = new BMapManager(getApplicationContext());
			/**
			 * ���BMapManagerû�г�ʼ�����ʼ��BMapManager
			 */
			app.mBMapManager.init(DemoApplication.strKey,new DemoApplication.MyGeneralListener());
		}
		setContentView(R.layout.routeplan);
		CharSequence titleLable="·�߹滮����";
		setTitle(titleLable);

		//����AVOS����
		initData();
		//��ʼ����ͼ
		mMapView = (MapView)findViewById(R.id.bmapView);
		mMapView.setBuiltInZoomControls(false);
		mMapView.getController().setZoom(12);
		mMapView.getController().enableClick(true);

		//��ʼ������
		mBtnTransit = (Button)findViewById(R.id.transit);
		mBtnWalk = (Button)findViewById(R.id.walk);
		mBtnPre = (Button)findViewById(R.id.pre);
		mBtnNext = (Button)findViewById(R.id.next);
		mSendRoute=(Button)findViewById(R.id.send_route);
		mSendRoute.setVisibility(View.INVISIBLE);
		mBtnPre.setVisibility(View.INVISIBLE);
		mBtnNext.setVisibility(View.INVISIBLE);

		//��������¼�
		OnClickListener clickListener = new OnClickListener(){
			@Override
			public void onClick(View v) {
				//��������
				startpoint=((EditText)findViewById(R.id.start)).getText().toString();
				endpoint=((EditText)findViewById(R.id.end)).getText().toString();
				SearchButtonProcess(v);
			}
		};
		OnClickListener nodeClickListener = new OnClickListener(){
			@Override
			public void onClick(View v) {
				//���·�߽ڵ�
				nodeClick(v);
			}
		};
		OnClickListener sendrouteClickListener = new OnClickListener(){
			@Override
			public void onClick(View v) {
				//����·��
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
		//���� ��������ͼ��
		createPaopao();

		//��ͼ����¼�����
		mMapView.regMapTouchListner(new MKMapTouchListener(){

			@Override
			public void onMapClick(GeoPoint point) {
				//�ڴ˴����ͼ����¼� 
				//����pop
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
		// ��ʼ������ģ�飬ע���¼�����
		mSearch = new MKSearch();
		mSearch.init(app.mBMapManager, new MKSearchListener(){

			@Override
			public void onGetTransitRouteResult(MKTransitRouteResult res,
					int error) {
				//�����յ������壬��Ҫѡ�����ĳ����б���ַ�б�
				if (error == MKEvent.ERROR_ROUTE_ADDR){
					//�������е�ַ
					//						ArrayList<MKPoiInfo> stPois = res.getAddrResult().mStartPoiList;
					//						ArrayList<MKPoiInfo> enPois = res.getAddrResult().mEndPoiList;
					//						ArrayList<MKCityListInfo> stCities = res.getAddrResult().mStartCityList;
					//						ArrayList<MKCityListInfo> enCities = res.getAddrResult().mEndCityList;
					return;
				}
				if (error != 0 || res == null) {
					Toast.makeText(MainActivity.this, "��Ǹ��δ�ҵ����", Toast.LENGTH_SHORT).show();
					return;
				}

				searchType = 1;
				transitOverlay = new TransitOverlay (MainActivity.this, mMapView);
				// �˴���չʾһ��������Ϊʾ��
				transitOverlay.setData(res.getPlan(0));
				//�������ͼ��
				mMapView.getOverlays().clear();
				//���·��ͼ��
				mMapView.getOverlays().add(transitOverlay);
				//ִ��ˢ��ʹ��Ч
				mMapView.refresh();
				// ʹ��zoomToSpan()���ŵ�ͼ��ʹ·������ȫ��ʾ�ڵ�ͼ��
				mMapView.getController().zoomToSpan(transitOverlay.getLatSpanE6(), transitOverlay.getLonSpanE6());
				//�ƶ���ͼ�����
				mMapView.getController().animateTo(res.getStart().pt);
				//����·�߽ڵ��������ڵ����ʱʹ��
				nodeIndex = 0;
				mBtnPre.setVisibility(View.VISIBLE);
				mBtnNext.setVisibility(View.VISIBLE);
				mSendRoute.setVisibility(View.VISIBLE);
			}

			@Override
			public void onGetWalkingRouteResult(MKWalkingRouteResult res,
					int error) {
				//�����յ������壬��Ҫѡ�����ĳ����б���ַ�б�
				if (error == MKEvent.ERROR_ROUTE_ADDR){
					//·�����������յ�������
					Toast.makeText(MainActivity.this, "��Ǹ�������յ������壬����������", Toast.LENGTH_SHORT).show();
					return;
				}
				if(error == MKEvent.ERROR_RESULT_NOT_FOUND){
					//δ�ҵ��������
					Toast.makeText(MainActivity.this, "��Ǹ��δ�ҵ����", Toast.LENGTH_SHORT).show();
					return;
				}
				// ����ſɲο�MKEvent�еĶ���
				if (error != 0 || res == null) {
					Toast.makeText(MainActivity.this, "��Ǹ��δ�ҵ����", Toast.LENGTH_SHORT).show();
					return;
				}

				searchType = 2;
				routeOverlay = new RouteOverlay(MainActivity.this, mMapView);
				// �˴���չʾһ��������Ϊʾ��
				routeOverlay.setData(res.getPlan(0).getRoute(0));
				//�������ͼ��
				mMapView.getOverlays().clear();
				//���·��ͼ��
				mMapView.getOverlays().add(routeOverlay);
				//ִ��ˢ��ʹ��Ч
				mMapView.refresh();
				// ʹ��zoomToSpan()���ŵ�ͼ��ʹ·������ȫ��ʾ�ڵ�ͼ��
				mMapView.getController().zoomToSpan(routeOverlay.getLatSpanE6(), routeOverlay.getLonSpanE6());
				//�ƶ���ͼ�����
				mMapView.getController().animateTo(res.getStart().pt);
				//��·�����ݱ����ȫ�ֱ���
				route = res.getPlan(0).getRoute(0);
				//����·�߽ڵ��������ڵ����ʱʹ��
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
	//����AVOS����
	void initData(){
		AVOSCloud.useAVCloudCN();
		AVOSCloud.initialize(this, "9f9kc7n2gfm4ug07arijhef5roy4kvq4l1xh6voy714hfswm", "2nuzsw9x0tw3mheybe7spewu43bfwnfoujry68g51973watr");
		PushService.setDefaultPushCallback(this, MainActivity.class);
		InstallationId=AVInstallation.getCurrentInstallation().getInstallationId();
		Log.d("state","this device id is "+InstallationId);
		AVInstallation.getCurrentInstallation().saveInBackground();

		//����_User���е�installationId
		AVUser user=new AVUser();
		AVUser.logInInBackground(username+"_parent", password, new LogInCallback<AVUser>() {

			@Override
			public void done(AVUser arg0, AVException arg1) {
				// TODO Auto-generated method stub
				if(arg1==null&&arg0!=null){
					Log.i("state","�ҳ���½�ɹ�");
					//					user.remove("installationid");
					arg0.put("installationid", InstallationId);
					Log.i("state","�洢installationid "+InstallationId);
					arg0.signUpInBackground(new SignUpCallback() {

						@Override
						public void done(AVException arg0) {
							// TODO Auto-generated method stub
							if (arg0 == null) {
								Log.i("state","�޸ĳɹ�");

								//��ȡĿ��installationid
								AVQuery<AVObject> query2 = new AVQuery<AVObject>("_User");
								query2.whereEqualTo("username", username+"_child");
								query2.findInBackground(new FindCallback<AVObject>() {

									@Override
									public void done(List<AVObject> avObjects, AVException e) {
										if (e == null) {
											Log.i("�ɹ�222", "��ѯ��" + avObjects.size() + " ����������������");
											targetID=avObjects.get(0).getString("installationid");
											Log.i("state222", "targetID is "+targetID);
										} else {
											Log.i("ʧ��222", "��ѯ����: " + e.getMessage());
										}
									}
								});
							} else {
								Log.i("state","�޸�ʧ��"+arg0.getCode());
							}
						}
					});
				}else if(arg0==null){
					Log.i("state","�û��������������");
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
	 * ����·�߹滮����ʾ��
	 * @param v
	 */
	void SearchButtonProcess(View v) {
		//��������ڵ��·������
		route = null;
		routeOverlay = null;
		transitOverlay = null; 
		mBtnPre.setVisibility(View.INVISIBLE);
		mBtnNext.setVisibility(View.INVISIBLE);
		mSendRoute.setVisibility(View.INVISIBLE);
		// ����������ť��Ӧ
		EditText editSt = (EditText)findViewById(R.id.start);
		EditText editEn = (EditText)findViewById(R.id.end);

		// ������յ��name���и�ֵ��Ҳ����ֱ�Ӷ����긳ֵ����ֵ�����򽫸��������������
		MKPlanNode stNode = new MKPlanNode();
		stNode.pt=pt;//�������Ϊ������������
//		stNode.name = editSt.getText().toString();
		MKPlanNode enNode = new MKPlanNode();
		enNode.name = editEn.getText().toString();

		// ʵ��ʹ�����������յ���н�����ȷ���趨
		if (mBtnWalk.equals(v)) {
			traffic=1;
			mSearch.walkingSearch(targetcity, stNode, targetcity, enNode);
		} else if (mBtnTransit.equals(v)) {
			traffic=0;
			mSearch.transitSearch(targetcity, stNode, enNode);
		} 
	}
	/**
	 * �ڵ����ʾ��
	 * @param v
	 */
	public void nodeClick(View v){
		viewCache = getLayoutInflater().inflate(R.layout.custom_text_view, null);
		popupText =(TextView) viewCache.findViewById(R.id.textcache);
		if (searchType == 0 || searchType == 2){
			//�ݳ�������ʹ�õ����ݽṹ��ͬ���������Ϊ�ݳ����У��ڵ����������ͬ
			if (nodeIndex < -1 || route == null || nodeIndex >= route.getNumSteps())
				return;

			//��һ���ڵ�
			if (mBtnPre.equals(v) && nodeIndex > 0){
				//������
				nodeIndex--;
				//�ƶ���ָ������������
				mMapView.getController().animateTo(route.getStep(nodeIndex).getPoint());
				//��������
				popupText.setBackgroundResource(R.drawable.popup);
				popupText.setText(route.getStep(nodeIndex).getContent());
				pop.showPopup(BMapUtil.getBitmapFromView(popupText),
						route.getStep(nodeIndex).getPoint(),
						5);
			}
			//��һ���ڵ�
			if (mBtnNext.equals(v) && nodeIndex < (route.getNumSteps()-1)){
				//������
				nodeIndex++;
				//�ƶ���ָ������������
				mMapView.getController().animateTo(route.getStep(nodeIndex).getPoint());
				//��������
				popupText.setBackgroundResource(R.drawable.popup);
				popupText.setText(route.getStep(nodeIndex).getContent());
				pop.showPopup(BMapUtil.getBitmapFromView(popupText),
						route.getStep(nodeIndex).getPoint(),
						5);
			}
		}
		if (searchType == 1){
			//��������ʹ�õ����ݽṹ��������ͬ����˵�������ڵ����
			if (nodeIndex < -1 || transitOverlay == null || nodeIndex >= transitOverlay.getAllItem().size())
				return;

			//��һ���ڵ�
			if (mBtnPre.equals(v) && nodeIndex > 1){
				//������
				nodeIndex--;
				//�ƶ���ָ������������
				mMapView.getController().animateTo(transitOverlay.getItem(nodeIndex).getPoint());
				//��������
				popupText.setBackgroundResource(R.drawable.popup);
				popupText.setText(transitOverlay.getItem(nodeIndex).getTitle());
				pop.showPopup(BMapUtil.getBitmapFromView(popupText),
						transitOverlay.getItem(nodeIndex).getPoint(),
						5);
			}
			//��һ���ڵ�
			if (mBtnNext.equals(v) && nodeIndex < (transitOverlay.getAllItem().size()-2)){
				//������
				nodeIndex++;
				//�ƶ���ָ������������
				mMapView.getController().animateTo(transitOverlay.getItem(nodeIndex).getPoint());
				//��������
				popupText.setBackgroundResource(R.drawable.popup);
				popupText.setText(transitOverlay.getItem(nodeIndex).getTitle());
				pop.showPopup(BMapUtil.getBitmapFromView(popupText),
						transitOverlay.getItem(nodeIndex).getPoint(),
						5);
			}
		}

	}
	/**
	 * ������������ͼ��
	 */
	public void createPaopao(){

		//���ݵ����Ӧ�ص�
		PopupClickListener popListener = new PopupClickListener(){
			@Override
			public void onClickedPopup(int index) {
				Log.v("click", "clickapoapo");
			}
		};
		pop = new PopupOverlay(mMapView,popListener);
	}
	/**
	 * ��ת����·��Activity
	 */
	public void intentToActivity(){
		//��ת������·����ʾdemo
		//			Intent intent = new Intent(this, CustomRouteOverlayDemo.class);
		//	    	startActivity(intent); 
	}
	//����ʱ��
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
