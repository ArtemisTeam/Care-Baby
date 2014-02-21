package com.example.care_baby_parent;

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
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

		TextView reg;
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
			reg=(TextView)findViewById(R.id.textView1);
			log=(Button)findViewById(R.id.signin_button);
			edt_name=(EditText)findViewById(R.id.username_edit);
			edt_psw=(EditText)findViewById(R.id.password_edit);
			user = new AVUser();
			
			String text1="ע��";
			SpannableString spannableString1=new SpannableString(text1);
			spannableString1.setSpan(new ClickableSpan() {  
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					username=edt_name.getText().toString();
					password=edt_psw.getText().toString();
					if(CheckValid(username)!=true)
					{
						//��ʾ�û�����ʽ����
						Toast.makeText(MainActivity.this, "�û�����ʽ����", Toast.LENGTH_LONG).show();
						return;
					}
					if(CheckValid(password)!=true)
					{
						//��ʾ�����ʽ����
						Toast.makeText(MainActivity.this, "�����ʽ����", Toast.LENGTH_LONG).show();
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
									Toast.makeText(MainActivity.this, "�û�������ʹ��", Toast.LENGTH_LONG).show();
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
												Toast.makeText(MainActivity.this, "�ҳ�ע��ɹ�", Toast.LENGTH_LONG).show();
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
												Toast.makeText(MainActivity.this, "����ע��ɹ�", Toast.LENGTH_LONG).show();
											}
										}
									});
								}
								else if(count==1)
								{
									Log.i("state","�û����Ѵ���");
									Toast.makeText(MainActivity.this, "�û����Ѵ���", Toast.LENGTH_LONG).show();
								}
							} else {
								// The request failed

							}
						}
					}
							);

				}
	            }, 0, text1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  
			
			reg.setText(spannableString1);  
	        reg.setMovementMethod(LinkMovementMethod.getInstance());  
	        
			log.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					username=edt_name.getText().toString();
					password=edt_psw.getText().toString();
					if(CheckValid(username)!=true)
					{
						//��ʾ�û�����ʽ����
						Toast.makeText(MainActivity.this, "�û�����ʽ����", Toast.LENGTH_LONG).show();
						return;
					}
					if(CheckValid(password)!=true)
					{
						//��ʾ�����ʽ����
						Toast.makeText(MainActivity.this, "�����ʽ����", Toast.LENGTH_LONG).show();
						return;
					}
					AVUser.logInInBackground(username+"_parent", password, new LogInCallback<AVUser>() {

						@Override
						public void done(AVUser arg0, AVException arg1) {
							// TODO Auto-generated method stub
							if(arg1==null&&arg0!=null){
								Log.i("state","�ҳ���½�ɹ�");
							Intent intent = new Intent(); 
							intent.setClass(MainActivity.this, fragment.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  //ע�Ȿ�е�FLAG����
							startActivity(intent);
							}else if(arg0==null){
								Log.i("state","�û��������������");
								Toast.makeText(MainActivity.this, "�û��������������", Toast.LENGTH_LONG).show();
							}
						}
					});

				}
			});

		}

		//�����û�������Ϊ��ĸ����
		@SuppressLint("NewApi")
		public boolean CheckValid(String str)
		{
			if(str.isEmpty())
			{
				return false;
			}

			for(int i=0; i<str.length();i++)
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
