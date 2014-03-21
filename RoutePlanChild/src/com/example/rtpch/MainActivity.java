package com.example.rtpch;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
/**
 * ��demo����չʾ��ν��мݳ������С�����·���������ڵ�ͼʹ��RouteOverlay��TransitOverlay����
 * ͬʱչʾ��ν��нڵ��������������
 *
 */
public class MainActivity extends Activity {
	
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
	//��ͼ���
	private int traffic;
	private String targetcity=null;
	private String endpoint=null;
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
		//��ʼ����ͼ
        mMapView = (MapView)findViewById(R.id.bmapView);
        mMapView.setBuiltInZoomControls(false);
        mMapView.getController().setZoom(12);
        mMapView.getController().enableClick(true);

        //��ʼ������
        mBtnPre = (Button)findViewById(R.id.pre);
        mBtnNext = (Button)findViewById(R.id.next);
        mBtnPre.setVisibility(View.INVISIBLE);
		mBtnNext.setVisibility(View.INVISIBLE);
	    
        //��������¼�
        OnClickListener nodeClickListener = new OnClickListener(){
			@Override
			public void onClick(View v) {
				//���·�߽ڵ�
				nodeClick(v);
			}
        };
        
        mBtnPre.setOnClickListener(nodeClickListener);
        mBtnNext.setOnClickListener(nodeClickListener);
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
			public void onGetDrivingRouteResult(MKDrivingRouteResult res,
					int error) {
				//�����յ������壬��Ҫѡ�����ĳ����б���ַ�б�
				if (error == MKEvent.ERROR_ROUTE_ADDR){
					//�������е�ַ
//					ArrayList<MKPoiInfo> stPois = res.getAddrResult().mStartPoiList;
//					ArrayList<MKPoiInfo> enPois = res.getAddrResult().mEndPoiList;
//					ArrayList<MKCityListInfo> stCities = res.getAddrResult().mStartCityList;
//					ArrayList<MKCityListInfo> enCities = res.getAddrResult().mEndCityList;
					return;
				}
				// ����ſɲο�MKEvent�еĶ���
				if (error != 0 || res == null) {
					Toast.makeText(MainActivity.this, "��Ǹ��δ�ҵ����", Toast.LENGTH_SHORT).show();
					return;
				}
			
				searchType = 0;
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
			}

			@Override
			public void onGetTransitRouteResult(MKTransitRouteResult res,
					int error) {
				//�����յ������壬��Ҫѡ�����ĳ����б���ַ�б�
				if (error == MKEvent.ERROR_ROUTE_ADDR){
					//�������е�ַ
//					ArrayList<MKPoiInfo> stPois = res.getAddrResult().mStartPoiList;
//					ArrayList<MKPoiInfo> enPois = res.getAddrResult().mEndPoiList;
//					ArrayList<MKCityListInfo> stCities = res.getAddrResult().mStartCityList;
//					ArrayList<MKCityListInfo> enCities = res.getAddrResult().mEndCityList;
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
			}

			@Override
			public void onGetWalkingRouteResult(MKWalkingRouteResult res,
					int error) {
				//�����յ������壬��Ҫѡ�����ĳ����б���ַ�б�
				if (error == MKEvent.ERROR_ROUTE_ADDR){
					//�������е�ַ
//					ArrayList<MKPoiInfo> stPois = res.getAddrResult().mStartPoiList;
//					ArrayList<MKPoiInfo> enPois = res.getAddrResult().mEndPoiList;
//					ArrayList<MKCityListInfo> stCities = res.getAddrResult().mStartCityList;
//					ArrayList<MKCityListInfo> enCities = res.getAddrResult().mEndCityList;
					return;
				}
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
			    
			}
			@Override
			public void onGetAddrResult(MKAddrInfo res, int error) {
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
        });
	}
	/**
	 * ����·�߹滮����ʾ��
	 * @param v
	 */
	void SearchRouteProcess(GeoPoint local,String end_point,String target_city) {
		//��������ڵ��·������
		route = null;
		routeOverlay = null;
		transitOverlay = null; 
		mBtnPre.setVisibility(View.INVISIBLE);
		mBtnNext.setVisibility(View.INVISIBLE);
		
		// ������յ��name���и�ֵ��Ҳ����ֱ�Ӷ����긳ֵ����ֵ�����򽫸��������������
		MKPlanNode stNode = new MKPlanNode();
		stNode.pt=local;
		MKPlanNode enNode = new MKPlanNode();
		enNode.name = end_point;

		// ʵ��ʹ�����������յ���н�����ȷ���趨
		if (traffic==1) {
			mSearch.walkingSearch(target_city, stNode, "target_city", enNode);
		} else if (traffic==0) {
			mSearch.transitSearch(target_city, stNode, enNode);
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
