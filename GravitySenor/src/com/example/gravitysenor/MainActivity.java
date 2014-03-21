package com.example.gravitysenor;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {
	private SensorManager mSensorManager=null;
	private Sensor mSensor=null;
	private static final String TAG = "sensor"; 
	private TextView tv=null;
	//计数器
	private int swingcount=0;//摇动次数计数器
	private int timercount=0;//timer执行计数器
	//三个方向上的加速度
	private float xacc=0;
	private float yacc=0;
	private float zacc=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tv=(TextView)findViewById(R.id.textView1);
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

		//给传感器绑定监听器  
		mSensorManager.registerListener(myAccelerometerListener,mSensor, SensorManager.SENSOR_DELAY_NORMAL);  

		Timer timer = new Timer(true);
		timer.schedule(task,1000, 333);
	}  

	TimerTask task = new TimerTask(){  
		@Override
		public void run() {  
			if(timercount==3){
				if(swingcount>=3){
					//危险发出报警！

				}
				swingcount=0;
				timercount=0;
			}else{
				timercount++;
				if(xacc>12||yacc>12||zacc>10){
					swingcount++;
				}
			}
		}  
	};  
	
	final SensorEventListener myAccelerometerListener = new SensorEventListener(){ 

		//复写onSensorChanged方法 
		@Override
		public void onSensorChanged(SensorEvent sensorEvent){ 
			if(sensorEvent.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){ 
				Log.i(TAG,"onSensorChanged"); 
				String text="";				
				float X_lateral = sensorEvent.values[0]; //X轴方向的加速
				float Y_longitudinal = sensorEvent.values[1]; //Y轴方向的加速
				float Z_vertical = sensorEvent.values[2]; //Z轴方向的加速
				xacc=X_lateral;
				yacc=Y_longitudinal;
				zacc=Z_vertical;

				Log.i(TAG,"\n heading "+X_lateral); 
				Log.i(TAG,"\n pitch "+Y_longitudinal); 
				Log.i(TAG,"\n roll "+Z_vertical); 
				text=""+X_lateral+'\n'+Y_longitudinal+'\n'+Z_vertical+'\n';
				tv.setText(text);
			} 
		} 
		//复写onAccuracyChanged方法 
		@Override
		public void onAccuracyChanged(Sensor sensor , int accuracy){ 
			Log.i(TAG, "onAccuracyChanged"); 
			tv.setText("onAccuracyChanged");
		} 
	}; 
	@Override  
	protected void onPause() {  
		//在这里需要将绑定的监听器释放，否则即使程序退出监听程序仍会继续  
		mSensorManager.unregisterListener(myAccelerometerListener);  
		super.onPause();  
	}  


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
