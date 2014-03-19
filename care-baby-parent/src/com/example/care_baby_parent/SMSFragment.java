package com.example.care_baby_parent;

import java.util.ArrayList;

import java.util.List;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.example.text_map_fragment.R;


import android.content.Context;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;


public class SMSFragment extends Fragment {
	
	private static List<SMSMsgEntity> mDataArrays = new ArrayList<SMSMsgEntity>();// ��Ϣ��������
	private SMSMsgViewAdapter mAdapter;
	String username=null;
	Button btSMS=null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		 View v = inflater.inflate(R.layout.main_sms, container, false);	
		 ListView ls1=(ListView)v.findViewById(R.id.listview);
		 
		 SharedPreferences preferences = getActivity().getSharedPreferences("userInfo",Context.MODE_PRIVATE);   
	     username = preferences.getString("username", "");  
	     
		 mAdapter = new SMSMsgViewAdapter(getActivity(), mDataArrays);
			ls1.setAdapter(mAdapter);
	        //mAdapter = new SMSMsgViewAdapter(getActivity(), mDataArrays);
	        initData();
	     btSMS=(Button)v.findViewById(R.id.btn_send);
	     btSMS.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
				// TODO �Զ����ɵķ������	
					mDataArrays.clear();
    				initData();
			}
	    	 
	     });
		return v;
	}
	
	public void initData() {
		AVQuery<AVObject> query = new AVQuery<AVObject>("SMS");
		query.whereEqualTo("username", username);
		query.orderByAscending("createdAt");
		query.findInBackground(new FindCallback<AVObject>() {
			
			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				// TODO Auto-generated method stub
				Log.i("state","��ȡ->"+arg0.size()+" ������" );
				for(int i=0;i<arg0.size();i++)
				{
				AVObject record=	arg0.get(i);
				String sender=record.getString("sender");
				Log.i("state", "�����˺�����->"+sender);
//				String sender_name=record.getString("sender_name");//��ȡ����������
//				Log.i("state", "�����˱�ע����->"+sender_name);//��ȡ����������
				String content=record.getString("content");
				Log.i("state", "��Ϣ������->"+content);
				String date=record.getCreatedAt().toLocaleString();

				SMSMsgEntity entity2 = new SMSMsgEntity();
    			entity2.setName(sender);//��¼ʱ���û���
    			entity2.setDate(date);
    			entity2.setMessage(content);
    			entity2.setMsgType(true);
    			mDataArrays.add(entity2);
    			mAdapter.notifyDataSetChanged();// ֪ͨListView�������ѷ����ı�
				}
			}
		});
	}
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	
}
