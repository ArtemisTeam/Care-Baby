package com.example.care_baby;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVPush;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SendCallback;
import com.avos.avoscloud.SignUpCallback;

public class mUtils{
	private String username=null;
	private String password=null;
	private String targetID=null;
	private String InstallationId=null;
	private String midentity;
	private String tidentity;
	public mUtils() {
		// TODO Auto-generated constructor stub
		InstallationId=AVInstallation.getCurrentInstallation().getInstallationId();
		AVInstallation.getCurrentInstallation().saveInBackground();
	}

	public void initial(String user,String pass,int flag){//flag=1 孩子 flag=2 家长
		username=user;
		password=pass;
		if(flag==1){
			midentity="_child";
			tidentity="_parent";
		}else if(flag==2){
			midentity="_parent";
			tidentity="_child";
		}
		//更新_User表中的installationId
		AVUser avu=new AVUser();
		AVUser.logInInBackground(username+midentity, password, new LogInCallback<AVUser>() {

			@Override
			public void done(AVUser arg0, AVException arg1) {
				// TODO Auto-generated method stub
				if(arg1==null&&arg0!=null){
					Log.i("state","登陆成功");
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
								query2.whereEqualTo("username", username+tidentity);
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
	}
	public void sendMSG(String msg) throws JSONException{
		AVQuery pushQuery = AVInstallation.getQuery();
		pushQuery.whereEqualTo("installationId", targetID);
		AVPush push = new AVPush();
		push.setQuery(pushQuery);
		JSONObject data=new JSONObject();
		data.put("action",  "com.avos.UPDATE_STATUS");
		data.put("send_time",getDate());
		data.put("msg",msg);
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
	private String getDate() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		return format.format(new Date());
	}
}
