package com.example.avosfile_download;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.GetDataCallback;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		AVOSCloud.useAVCloudCN();
		AVOSCloud.initialize(this, "9f9kc7n2gfm4ug07arijhef5roy4kvq4l1xh6voy714hfswm", "2nuzsw9x0tw3mheybe7spewu43bfwnfoujry68g51973watr");
		AVFile file=new AVFile("test.png","http://paas-files.qiniudn.com/OMOMJHYVTM31YWda857520hqolCAxvMJHEGEC1hf.jpg");//第二个参数是Url
		file.getDataInBackground(new GetDataCallback() {

			@Override
			public void done(byte[] arg0, AVException arg1) {
				// TODO Auto-generated method stub
				if(arg1==null){
					File f=new File("/data/data/com.example.avosfile_download/test1.png");
					if(!f.exists())
					{
						try {
							f.createNewFile();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out.println("文件"+f.getPath()+"已创建");
					}
					FileOutputStream fos = null;
					try {
						fos = new FileOutputStream(f);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						fos.write(arg0);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
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
