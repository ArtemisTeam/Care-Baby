package com.example.care_baby_parent;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.example.text_map_fragment.R;


import android.content.Context;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

public class phoneFragment extends Fragment {

	private static List<PhoneEntity> mDataArrays = new ArrayList<PhoneEntity>();// 消息对象数组
	private phoneViewAdpter mAdapter;
	String username=null;
	Button btSMS=null;

	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        AVOSCloud.useAVCloudCN();
			AVOSCloud.initialize(getActivity(), "9f9kc7n2gfm4ug07arijhef5roy4kvq4l1xh6voy714hfswm", "2nuzsw9x0tw3mheybe7spewu43bfwnfoujry68g51973watr");
//			AVAnalytics.trackAppOpened(getActivity().getIntent());
	        
	 }
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		 View v = inflater.inflate(R.layout.main_sms, container, false);	
		 ListView ls1=(ListView)v.findViewById(R.id.listview);
		 
		 SharedPreferences preferences = getActivity().getSharedPreferences("userInfo",Context.MODE_PRIVATE);   
	     username = preferences.getString("username", "");  
	     
		 mAdapter = new phoneViewAdpter(getActivity(), mDataArrays);
			ls1.setAdapter(mAdapter);
	        mAdapter = new phoneViewAdpter(getActivity(), mDataArrays);
	        initData();
	     btSMS=(Button)v.findViewById(R.id.btn_send);
	     btSMS.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
				// TODO 自动生成的方法存根	
					mDataArrays.clear();
    				initData();
			}
	    	 
	     });
		return v;
	}
	
	public void initData() {
		
		AVQuery<AVObject> query = new AVQuery<AVObject>("CallHistory");
		query.whereEqualTo("username", username);
		query.orderByAscending("createdAt");
		query.findInBackground(new FindCallback<AVObject>() {
			
			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				// TODO Auto-generated method stub
				Log.i("state","获取->"+arg0.size()+" 条通话记录" );
				for(int i=0;i<arg0.size();i++)
				{
				AVObject record=arg0.get(i);
				String caller_name=record.getString("caller_name");
				Log.i("state", "联系人姓名是->"+caller_name);
				String phonenumber=record.getString("phonenumber");
				Log.i("state", "联系人号码是->"+phonenumber);
				String calltime=(record.getString("calltime"));
				Log.i("state", "拨号时间是->"+calltime);
				int flag=record.getInt("FLAG");
				if(flag==1)
				{//来电
					phonenumber="呼入号码为："+phonenumber;
				}
				if(flag==2)
				{//已播
					phonenumber="呼出号码为："+phonenumber;
				}
				if(flag==3)
				{//未接
					phonenumber="未接号码为："+phonenumber;
				}
				PhoneEntity entity2 = new PhoneEntity();
    			entity2.setName(caller_name);
    			entity2.setDate(calltime);
    			entity2.setMessage(phonenumber);
    			entity2.setMsgType(true);
    			mDataArrays.add(entity2);
    			mAdapter.notifyDataSetChanged();// 通知ListView，数据已发生改变
				}
			}

		});
		
		/*ContentResolver cr = getActivity().getContentResolver();
		final Cursor cursor = cr.query(CallLog.Calls.CONTENT_URI, new String[]{CallLog.Calls.NUMBER,CallLog.Calls.CACHED_NAME,CallLog.Calls.TYPE, CallLog.Calls.DATE}, null, null,CallLog.Calls.DEFAULT_SORT_ORDER);
		for (int i = 0; i < cursor.getCount(); i++) {   

			String str = "";
			String num ="";
			int type;
			String time;
			Date date;
			long duration ;
			cursor.moveToPosition(i);
			num = cursor.getString(0);
			str = cursor.getString(1);
			type = cursor.getInt(2);
			duration = cursor.getInt(3);
			SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	         date = new Date(Long.parseLong(cursor.getString(3)));
	         time = sfd.format(date);
			if(str==null)
			{
				str="未知";
			}
	
			PhoneEntity entity2 = new PhoneEntity();
			entity2.setName(str);
			entity2.setDate(time);
			entity2.setMessage(num);
			entity2.setMsgType(true);
			mDataArrays.add(entity2);
			mAdapter.notifyDataSetChanged();// 通知ListView，数据已发生改变
		}*/
		
	}
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	
	private String timeToString(Long millisecond){
		String str = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Timestamp ts = new Timestamp(millisecond);
		str = sdf.format(ts);
		return str;
	}
	
	private String getPeopleNameFromPerson(String address){  
        if(address == null || address == ""){  
            return null;  
        }  
          
        String strPerson = "null";  
        String[] projection = new String[] {Phone.DISPLAY_NAME, Phone.NUMBER};  
          
        Uri uri_Person = Uri.withAppendedPath(ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI, address);  // address 手机号过滤  
        Cursor cursor = getActivity().getContentResolver().query(uri_Person, projection, null, null, null);  
          
        if(cursor.moveToFirst()){  
            int index_PeopleName = cursor.getColumnIndex(Phone.DISPLAY_NAME);  
            String strPeopleName = cursor.getString(index_PeopleName);  
            strPerson = strPeopleName;  
        }  
        else{  
            strPerson = "未知号码:"+address;  
        }  
        cursor.close();  
        cursor=null;  
        return strPerson;  
    }  
	
}
