package com.example.care_babby;
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
import android.util.Log;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.SendCallback;
import com.avos.avoscloud.SignUpCallback;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
/**
 * @author way
 */
public class talk extends Activity implements OnClickListener {
	   private static final String TAG = "MyCustomReceiver";
	    private final String ACTION_NAME= "发送广播"; 
	    String message=null;
	    String date=null;

		private Button mBtnSend;// 发送btn
		private Button mBtnBack;// 返回btn
		private EditText mEditTextContent;
		private ListView mListView;
		private ChatMsgViewAdapter mAdapter;// 消息视图的Adapter
		private List<ChatMsgEntity> mDataArrays = new ArrayList<ChatMsgEntity>();// 消息对象数组

		String username=null;//登陆用户基本名家长端需要加后缀_parent
		String password=null;
		String targetID=null;
		String InstallationId=null;
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.main);
			
			Intent intent=getIntent();
			Bundle bundle=intent.getExtras();
			username=bundle.getString("user");
			password=bundle.getString("pass");
			IntentFilter filter=new IntentFilter();
			filter.addAction("com.avos.UPDATE_STATUS");
			talk.this.registerReceiver(mBroadcastReceiver, filter);
			
			initView();// 初始化view

			initData();// 初始化数据
			mListView.setSelection(mAdapter.getCount() - 1);
		}

	    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver(){ 

		    @Override
		    public void onReceive(Context context, Intent intent) {

		        Log.d(TAG, "Get Broadcat");
		        
		        try {
		            String action = intent.getAction();
		            String channel = intent.getExtras().getString("com.avos.avoscloud.Channel");
		            JSONObject json = new JSONObject(intent.getExtras().getString("com.avos.avoscloud.Data"));
		            Log.d(TAG, "got action " + action + " on channel " + channel + " with:");
		            Iterator itr = json.keys();
		            while (itr.hasNext()) {
		                String key = (String) itr.next();
		                Log.d(TAG, ".../" + key + "/ => " + json.getString(key));//key=msgʱ�������յ�����Ϣ���Ż������activity
		                if (key.equals("msg"))
		                {
		                	
		                	message=json.getString("msg");
		                	date=json.getString("send_time");
		                	Log.i(message, date);
		                	
		                	ChatMsgEntity entity2 = new ChatMsgEntity();
		        			entity2.setName("家长");//登录时的用户名
		        			entity2.setDate(date);
		        			entity2.setMessage(message);
		        			entity2.setMsgType(true);
		        			mDataArrays.add(entity2);
		        			mAdapter.notifyDataSetChanged();// 通知ListView，数据已发生改变
		        			mListView.setSelection(mListView.getCount() - 1);// 发送一条消息时，ListView显示选择最后一项
		                }else {
		                	Log.i("1", "1");
						}
		            }
		        } catch (JSONException e) {
		            Log.d(TAG, "JSONException: " + e.getMessage());
		        }
		    }
		};
		/**
		 * 初始化view
		 */
		public void initView() {
			mListView = (ListView) findViewById(R.id.listview);
			mBtnSend = (Button) findViewById(R.id.btn_send);
			mBtnSend.setOnClickListener(this);
			mEditTextContent = (EditText) findViewById(R.id.et_sendmessage);
		}


		/**
		 * 模拟加载消息历史，实际开发可以从数据库中读出
		 */
		public void initData() {
			AVOSCloud.useAVCloudCN();
			AVOSCloud.initialize(this, "9f9kc7n2gfm4ug07arijhef5roy4kvq4l1xh6voy714hfswm", "2nuzsw9x0tw3mheybe7spewu43bfwnfoujry68g51973watr");
			AVAnalytics.trackAppOpened(getIntent());
			PushService.setDefaultPushCallback(this, talk.class);
			InstallationId=AVInstallation.getCurrentInstallation().getInstallationId();
			Log.i("state","this device id is "+InstallationId);
			AVInstallation.getCurrentInstallation().saveInBackground();

			//更新_User表中的installationId
			AVUser user=new AVUser();
			AVUser.logInInBackground(username+"_child", password, new LogInCallback<AVUser>() {

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
									query2.whereEqualTo("username", username+"_parent");
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



			mAdapter = new ChatMsgViewAdapter(this, mDataArrays);
			mListView.setAdapter(mAdapter);
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_send:// 发送按钮点击事件
				try {
					send();
				} catch (JSONException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
				break;
			}
		}

		/**
		 * 发送消息
		 * @throws JSONException 
		 */
		private void send() throws JSONException {
			String contString = mEditTextContent.getText().toString();
			if (contString.length() > 0) {
				ChatMsgEntity entity = new ChatMsgEntity();
				entity.setName("孩子");//登录时的用户名
				entity.setDate(getDate());
				entity.setMessage(contString);
				entity.setMsgType(false);
				mDataArrays.add(entity);
				
				
				
				mAdapter.notifyDataSetChanged();// 通知ListView，数据已发生改变
				mEditTextContent.setText("");// 清空编辑框数
				mListView.setSelection(mListView.getCount() - 1);// 发送一条消息时，ListView显示选择最后一项

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
		 * 发送消息时，获取当前事件
		 * 
		 * @return 当前时间
		 */
		private String getDate() {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			return format.format(new Date());
		}

		@Override
		protected void onDestroy(){
			super.onDestroy();
		}
}