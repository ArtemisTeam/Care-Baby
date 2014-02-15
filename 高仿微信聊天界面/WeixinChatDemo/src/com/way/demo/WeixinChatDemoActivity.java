package com.way.demo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.alibaba.fastjson.JSONObject;
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

/**
 * @author way
 */
public class WeixinChatDemoActivity extends Activity implements OnClickListener {

	private Button mBtnSend;// 发送btn
	private Button mBtnBack;// 返回btn
	private EditText mEditTextContent;
	private ListView mListView;
	private ChatMsgViewAdapter mAdapter;// 消息视图的Adapter
	private List<ChatMsgEntity> mDataArrays = new ArrayList<ChatMsgEntity>();// 消息对象数组

	String username="211";//登陆用户基本名家长端需要加后缀_parent
	String password="12345";
	String targetID=null;
	String InstallationId=null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);


		initView();// 初始化view

		initData();// 初始化数据
		mListView.setSelection(mAdapter.getCount() - 1);
	}

	/**
	 * 初始化view
	 */
	public void initView() {
		mListView = (ListView) findViewById(R.id.listview);
		mBtnSend = (Button) findViewById(R.id.btn_send);
		mBtnSend.setOnClickListener(this);
		mBtnBack = (Button) findViewById(R.id.btn_back);
		mBtnBack.setOnClickListener(this);
		mEditTextContent = (EditText) findViewById(R.id.et_sendmessage);
	}

	private String[] msgArray = new String[] { "有大吗", "有！你呢？", "我也有", "那上吧",
			"打啊！你放大啊！", "你TM咋不放大呢？留大抢人头啊？CAO！你个菜B", "2B不解释", "尼滚...",
			"今晚去网吧包夜吧？", "有毛片吗？", "种子一大堆啊~还怕没片？", "OK,搞起！！" };

	private String[] dataArray = new String[] { "2012-09-22 18:00:02",
			"2012-09-22 18:10:22", "2012-09-22 18:11:24",
			"2012-09-22 18:20:23", "2012-09-22 18:30:31",
			"2012-09-22 18:35:37", "2012-09-22 18:40:13",
			"2012-09-22 18:50:26", "2012-09-22 18:52:57",
			"2012-09-22 18:55:11", "2012-09-22 18:56:45",
			"2012-09-22 18:57:33", };
	private final static int COUNT = 12;// 初始化数组总数

	/**
	 * 模拟加载消息历史，实际开发可以从数据库中读出
	 */
	public void initData() {
		AVOSCloud.useAVCloudCN();
		AVOSCloud.initialize(this, "9f9kc7n2gfm4ug07arijhef5roy4kvq4l1xh6voy714hfswm", "2nuzsw9x0tw3mheybe7spewu43bfwnfoujry68g51973watr");
		AVAnalytics.trackAppOpened(getIntent());
		PushService.setDefaultPushCallback(this, WeixinChatDemoActivity.class);
		InstallationId=AVInstallation.getCurrentInstallation().getInstallationId();
		Log.i("state","this device id is "+InstallationId);
		AVInstallation.getCurrentInstallation().saveInBackground();

		//更新_User表中的installationId
		AVUser user=new AVUser();
		user.logInInBackground(username+"_parent", password, new LogInCallback<AVUser>() {

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

		for (int i = 0; i < COUNT; i++) {
			ChatMsgEntity entity = new ChatMsgEntity();
			entity.setDate(dataArray[i]);
			if (i % 2 == 0) {
				entity.setName("孩子");
				entity.setMsgType(true);// 收到的消息
			} else {
				entity.setName(username);
				entity.setMsgType(false);// 自己发送的消息
			}
			entity.setMessage(msgArray[i]);
			mDataArrays.add(entity);
		}

		mAdapter = new ChatMsgViewAdapter(this, mDataArrays);
		mListView.setAdapter(mAdapter);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_send:// 发送按钮点击事件
			send();
			break;
		case R.id.btn_back:// 返回按钮点击事件
			finish();// 结束,实际开发中，可以返回主界面
			break;
		}
	}

	/**
	 * 发送消息
	 */
	private void send() {
		String contString = mEditTextContent.getText().toString();
		if (contString.length() > 0) {
			ChatMsgEntity entity = new ChatMsgEntity();
			entity.setName(username);//登录时的用户名
			entity.setDate(getDate());
			entity.setMessage(contString);
			entity.setMsgType(false);

			mDataArrays.add(entity);
			mAdapter.notifyDataSetChanged();// 通知ListView，数据已发生改变

			mEditTextContent.setText("");// 清空编辑框数据

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
}