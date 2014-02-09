package com.login.register;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SignUpCallback;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {
	Button reg=null;
	Button log=null;
	EditText edt_name=null;
	EditText edt_psw=null;
	String username=null;
	String password=null;
	AVUser user=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AVOSCloud.useAVCloudCN();
		AVOSCloud.initialize(this, "9f9kc7n2gfm4ug07arijhef5roy4kvq4l1xh6voy714hfswm", "2nuzsw9x0tw3mheybe7spewu43bfwnfoujry68g51973watr");
		AVAnalytics.trackAppOpened(getIntent());
		setContentView(R.layout.activity_main);
		reg=(Button)findViewById(R.id.button_reg);
		log=(Button)findViewById(R.id.button_log);
		edt_name=(EditText)findViewById(R.id.edit_name);
		edt_psw=(EditText)findViewById(R.id.edit_psw);
		user = new AVUser();
		reg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				username=edt_name.getText().toString();
				password=edt_psw.getText().toString();
				if(CheckValid(username)!=true)
				{
					//��ʾ�û�����ʽ����
					return;
				}
				if(CheckValid(password)!=true)
				{
					//��ʾ�����ʽ����
					return;
				}
				AVQuery<AVObject> query = new AVQuery<AVObject>("_User");
				query.whereEqualTo("username", username+"_parent");
				query.countInBackground(new CountCallback() {
					@Override
					public void done(int count, AVException e) {
						if (e == null) {
							// The count request succeeded. Log the count
							if(count==0)
							{
								Log.i("state","�û�������ʹ��");
								//�ҳ�ע�ᣬ˳����installationid
								user.setUsername(username+"_parent");
								user.setPassword(password);
								String InstallationId=AVInstallation.getCurrentInstallation().getInstallationId();
								user.put("installationid", InstallationId);
								user.put("phone", "");//�绰����
								user.signUpInBackground(new SignUpCallback() {

									@Override
									public void done(AVException arg0) {
										// TODO Auto-generated method stub
										if(arg0==null){
											Log.i("state","�ҳ�ע��ɹ�");
										}
									}
								});
								//����ע�ᣬinstallationid�ں��Ӷ˵�½ʱ��
								user.setUsername(username+"_child");
								user.setPassword(password);
								user.put("installationid", "");
								user.put("phone", "");//�绰����
								user.signUpInBackground(new SignUpCallback() {

									@Override
									public void done(AVException arg0) {
										// TODO Auto-generated method stub
										if(arg0==null){
											Log.i("state","����ע��ɹ�");
										}
									}
								});
							}
							else if(count==1)
							{
								Log.i("state","�û����Ѵ���");
							}
						} else {
							// The request failed

						}
					}
				}
						);

			}
		});

		log.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				username=edt_name.getText().toString();
				password=edt_psw.getText().toString();
				if(CheckValid(username)!=true)
				{
					//��ʾ�û�����ʽ����
					return;
				}
				if(CheckValid(password)!=true)
				{
					//��ʾ�����ʽ����
					return;
				}
				user.logInInBackground(username+"_parent", password, new LogInCallback<AVUser>() {

					@Override
					public void done(AVUser arg0, AVException arg1) {
						// TODO Auto-generated method stub
						if(arg1==null&&arg0!=null){
						Log.i("state","�ҳ���½�ɹ�");
						}else if(arg0==null){
							Log.i("state","�û��������������");
						}
						
					}
				});

			}
		});

	}

	//�����û�������Ϊ��ĸ����
	public boolean CheckValid(String str)
	{
		if(str.isEmpty())
		{
			return false;
		}

		for(int i=0;i<str.length();i++)
			if( Character.isLetterOrDigit(str.charAt(i))!=true )
			{
				return false;
			}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
