package com.example.care_baby_parent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


import org.json.JSONException;
import org.json.JSONObject;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.util.Log;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVPush;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.LogInCallback;

import com.avos.avoscloud.SendCallback;
import com.avos.avoscloud.SignUpCallback;
import com.example.text_map_fragment.R;

public class talk extends Fragment {
	private static final String TAG = "A";

    String message=null;
    String date=null;
	private Button mBtnSend;// 发�?btn
	private EditText mEditTextContent;
	private ListView mListView;
	private ChatMsgViewAdapter mAdapter;// 消息视图的Adapter
	private static List<ChatMsgEntity> mDataArrays = new ArrayList<ChatMsgEntity>();// 消息对象数组

	String username=null;//登陆用户基本名家长端�?��加后�?parent
	String password=null;
	String targetID=null;
	String InstallationId=null;
	BroadcastReceiver mItemViewListClickReceiver;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	        View v = inflater.inflate(R.layout.main, container, false);
	        SharedPreferences preferences = getActivity().getSharedPreferences("userInfo",Context.MODE_PRIVATE);   
	        username = preferences.getString("username", "");  
	        password = preferences.getString("password", "");  
	        mListView = (ListView) v.findViewById(R.id.listview);
	    	
			mBtnSend = (Button) v.findViewById(R.id.btn_send);
			mBtnSend.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO 自动生成的方法存�?
					try {
						send();
					} catch (JSONException e) {
						// TODO 自动生成�?catch �?
						e.printStackTrace();
					}
				}
					});
			mEditTextContent = (EditText) v.findViewById(R.id.et_sendmessage);
			
			mAdapter = new ChatMsgViewAdapter(getActivity(), mDataArrays);
			mListView.setAdapter(mAdapter);
			mListView.setSelection(mAdapter.getCount() - 1);
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction("com.avos.UPDATE_STATUS");//建议把它写一个公共的变量，这里方便阅读就不写了�?
			mItemViewListClickReceiver = new BroadcastReceiver() {
			            @Override
			            public void onReceive(Context context, Intent intent){

			            	Log.i("收到广播","处理");
			            	try {
					            String action = intent.getAction();
					            String channel = intent.getExtras().getString("com.avos.avoscloud.Channel");
					            JSONObject json = new JSONObject(intent.getExtras().getString("com.avos.avoscloud.Data"));
					            Log.d(TAG, "got action " + action + " on channel " + channel + " with:");
					            Iterator itr = json.keys();
					            while (itr.hasNext()) {
					                String key = (String) itr.next();
					                Log.d(TAG, "..." + key + " => " + json.getString(key));//key=msgʱ�������յ�����Ϣ���Ż������activity
					                if (key.equals("msg"))
					                {
					                	
					                	message=json.getString("msg");
					                	date=json.getString("send_time");
					                	Log.i(message, message);
					                	Log.i(date, date);

					        			ChatMsgEntity entity2 = new ChatMsgEntity();
					        			entity2.setName("孩子");//登录时的用户�?
					        			entity2.setDate(date);
					        			entity2.setMessage(message);
					        			entity2.setMsgType(true);
					        			mDataArrays.add(entity2);
					        			mAdapter.notifyDataSetChanged();// 通知ListView，数据已发生改变
					        			mListView.setSelection(mListView.getCount() - 1);// 发�?�?��消息时，ListView显示选择�?���?��
		
					                }else {
					                	Log.i("1123", "1");
									}
					            }
					        } catch (JSONException e) {
					            Log.d(TAG, "JSONException: " + e.getMessage());
					        }
			            }

			 };
			 getActivity().registerReceiver(mItemViewListClickReceiver, intentFilter);
			 
			 initData();// 初始化数�?
			return v;
	 }
	
	 
	public void initData() {
		AVOSCloud.useAVCloudCN();
		AVOSCloud.initialize(getActivity(), "9f9kc7n2gfm4ug07arijhef5roy4kvq4l1xh6voy714hfswm", "2nuzsw9x0tw3mheybe7spewu43bfwnfoujry68g51973watr");
		AVAnalytics.trackAppOpened(getActivity().getIntent());
		
		InstallationId=AVInstallation.getCurrentInstallation().getInstallationId();
		Log.i("state","this device id is "+InstallationId);
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
											Log.i("成功222", "查询�? + avObjects.size() + " 条符合条件的数据");
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
					Log.i("state","用户名或者密码错�?);
				}
			}
		});

	
	}

	/**
	 * 发�?消息
	 * @throws JSONException 
	 */
	private void send() throws JSONException {
		String contString = mEditTextContent.getText().toString();
		if (contString.length() > 0) {
			ChatMsgEntity entity = new ChatMsgEntity();
			entity.setName("家长");//登录时的用户�?
			entity.setDate(getDate());
			entity.setMessage(contString);
			entity.setMsgType(false);

			mDataArrays.add(entity);
			mAdapter.notifyDataSetChanged();// 通知ListView，数据已发生改变

			mEditTextContent.setText("");// 清空编辑框数�?

			mListView.setSelection(mListView.getCount() - 1);// 发�?�?��消息时，ListView显示选择�?���?��

			AVQuery pushQuery = AVInstallation.getQuery();
			pushQuery.whereEqualTo("installationId", targetID);
			AVPush push = new AVPush();
			push.setQuery(pushQuery);
			JSONObject data=new JSONObject();
			data.put("action",  "com.avos.UPDATE_STATUS");
			data.put("send_time",getDate());
			data.put("msg",contString);
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
	}

	/**
	 * 发�?消息时，获取当前事件
	 * 
	 * @return 当前时间
	 */
	private String getDate() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		return format.format(new Date());
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		getActivity().unregisterReceiver(mItemViewListClickReceiver);
	}

}
