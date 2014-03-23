package com.example.avosfile;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		AVOSCloud.useAVCloudCN();
		AVOSCloud.initialize(this, "9f9kc7n2gfm4ug07arijhef5roy4kvq4l1xh6voy714hfswm", "2nuzsw9x0tw3mheybe7spewu43bfwnfoujry68g51973watr");
		GetandSaveCurrentImage();
//		uploadfile();
	}
	/**
	 * ��ȡ�ͱ��浱ǰ��Ļ�Ľ�ͼ
	 */
	private void GetandSaveCurrentImage()  
	{  
		//����Bitmap  
		WindowManager windowManager = getWindowManager();  
		Display display = windowManager.getDefaultDisplay();  
		int w = display.getWidth();  
		int h = display.getHeight();  
		Bitmap Bmp = Bitmap.createBitmap( w, h, Config.ARGB_8888 );      
		//��ȡ��Ļ  
		View decorview = this.getWindow().getDecorView();   
		decorview.setDrawingCacheEnabled(true);   
		Bmp = decorview.getDrawingCache();   
		//ͼƬ�洢·��
		String SavePath = "/data/data/com.example.avosfile/files";
		//����Bitmap   
		try {  
			File path = new File(SavePath);  
			//�ļ�  
			String filepath = SavePath + "/test.png";  
			File file = new File(filepath);  
			if(!path.exists()){  
				path.mkdirs();  
			}  
			if (!file.exists()) {  
				file.createNewFile();  
			}  
			FileOutputStream fos = null;  
			fos = new FileOutputStream(file);  
			if (null != fos) {  
				Bmp.compress(Bitmap.CompressFormat.PNG, 90, fos);  
				fos.flush();  
				fos.close();    

			}  
		} catch (Exception e) {  
			e.printStackTrace();  
		}  
	}  
	private void uploadfile(){
		AVFile avFile;
		try{
		       AVObject avObject = new AVObject("Screenshot");
		       avFile = new AVFile("test.png","/data/data/com.example.avosfile/files/test.png");
		       avFile.save();
		       avObject.put("username","�û���");
		       avObject.saveInBackground();
		}catch(AVException  e){
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
