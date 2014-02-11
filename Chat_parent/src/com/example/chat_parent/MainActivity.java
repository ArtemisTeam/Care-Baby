package com.example.chat_parent;

import java.text.SimpleDateFormat;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.SendCallback;
import com.avos.avoscloud.SignUpCallback;
import com.avos.security.auth.callback.Callback;

public class MainActivity extends Activity {
	Button btnsend;
	EditText edt;
	TextView tv;
	String username="211";//��½�û��������ҳ�����Ҫ�Ӻ�׺_parent
	String password="12345";
	String targetID=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AVOSCloud.useAVCloudCN();
		AVOSCloud.initialize(this, "9f9kc7n2gfm4ug07arijhef5roy4kvq4l1xh6voy714hfswm", "2nuzsw9x0tw3mheybe7spewu43bfwnfoujry68g51973watr");
		AVAnalytics.trackAppOpened(getIntent());
		PushService.setDefaultPushCallback(this, MainActivity.class);
		Log.i("state","this device id is "+AVInstallation.getCurrentInstallation().getInstallationId() );
		AVInstallation.getCurrentInstallation().saveInBackground();
		setContentView(R.layout.activity_main);
		btnsend=(Button)findViewById(R.id.button1);
		edt=(EditText)findViewById(R.id.editText1);
		tv=(TextView)findViewById(R.id.textView1);
		tv.setMovementMethod(ScrollingMovementMethod.getInstance() );

		//����_User���е�installationId
		AVUser user=new AVUser();
		user.logInInBackground(username+"_parent", password, new LogInCallback<AVUser>() {

			@Override
			public void done(AVUser arg0, AVException arg1) {
				// TODO Auto-generated method stub
				if(arg1==null&&arg0!=null){
					Log.i("state","�ҳ���½�ɹ�");
					String InstallationId=AVInstallation.getCurrentInstallation().getInstallationId();
					//					user.remove("installationid");
					arg0.put("installationid", InstallationId);
					Log.i("state","�洢installationid "+InstallationId);
					arg0.signUpInBackground(new SignUpCallback() {

						@Override
						public void done(AVException arg0) {
							// TODO Auto-generated method stub
							if (arg0 == null) {
								Log.i("state","�޸ĳɹ�");
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

		//��ȡĿ��installationid
		AVQuery<AVObject> query2 = new AVQuery<AVObject>("_User");
		query2.whereEqualTo("username", username+"_child");
		query2.findInBackground(new FindCallback<AVObject>() {
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
		btnsend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (edt.getText().toString().trim().equals("" )|| edt.getText().toString().trim()==null)return;
				SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				String date = sDateFormat.format(new java.util.Date());
				String tvBuffer=tv.getText().toString();
				tvBuffer=tvBuffer.concat(date+"\n");
				String InputMSG=edt.getText().toString();
				tvBuffer=tvBuffer.concat(InputMSG+"\n");
				tv.setText(tvBuffer);
				//						getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
				//				                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

				AVQuery pushQuery = AVInstallation.getQuery();
				pushQuery.whereEqualTo("installationId", targetID);
				AVPush.sendMessageInBackground(InputMSG,  pushQuery, new SendCallback() {
					@Override
					public void done(AVException e) {
						if (e == null) {
							Log.i("push", "success!");
						} else {
							Log.i("push", "failure");
						}
					}
				});

				//				AVQuery pushQuery = AVInstallation.getQuery();
				//				pushQuery.whereEqualTo("installationId", targetID);
				//				AVPush push = new AVPush();
				//				push.setQuery(pushQuery);
				//				JSONObject data=new JSONObject();
				//				data.put("action",  "com.avos.UPDATE_STATUS");
				//				data.put("msg", InputMSG);
				//				push.setData(data);
				//				push.sendInBackground(new SendCallback() {
				//					
				//					@Override
				//					public void done(AVException e) {
				//						// TODO Auto-generated method stub
				//						 if (e == null) {
				//				               Log.i("push", "success!");
				//				           } else {
				//				               Log.i("push", "failure");
				//				           }
				//					}
				//				});

				edt.setText("");

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
