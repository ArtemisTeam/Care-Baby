package com.example.login_child;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SignUpCallback;

public class MainActivity extends Activity {
	EditText edt_name=null;
	EditText edt_psw=null;
	Button log=null;
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
		edt_name=(EditText)findViewById(R.id.username_edit);
		edt_psw=(EditText)findViewById(R.id.password_edit);
		log=(Button)findViewById(R.id.signin_button);
		user = new AVUser();
		log.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				username=edt_name.getText().toString();
				password=edt_psw.getText().toString();
				if(CheckValid(username)!=true)
				{
					//提示用户名格式错误
					return;
				}
				if(CheckValid(password)!=true)
				{
					//提示密码格式错误
					return;
				}
				AVUser.logInInBackground(username+"_child", password, new LogInCallback<AVUser>() {

					@Override
					public void done(AVUser arg0, AVException arg1) {
						// TODO Auto-generated method stub
						if(arg1==null&&arg0!=null){
							Log.i("state","孩子登陆成功");
							String InstallationId=AVInstallation.getCurrentInstallation().getInstallationId();
//							user.remove("installationid");
							arg0.put("installationid", InstallationId);
							Log.i("state","存储installationid "+InstallationId);
							arg0.signUpInBackground(new SignUpCallback() {
								
								@Override
								public void done(AVException arg0) {
									// TODO Auto-generated method stub
									 if (arg0 == null) {
										 Log.i("state","修改成功");
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

			}
		});
	}
	//限制用户名密码为字母数字
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
