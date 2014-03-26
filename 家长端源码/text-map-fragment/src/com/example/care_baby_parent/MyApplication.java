package com.example.care_baby_parent;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;

public class MyApplication extends Application 
{ 
	   public void onCreate() { 
	     AVOSCloud.useAVCloudCN();
	     AVOSCloud.initialize(this, "9f9kc7n2gfm4ug07arijhef5roy4kvq4l1xh6voy714hfswm", "2nuzsw9x0tw3mheybe7spewu43bfwnfoujry68g51973watr");	

	   }
}