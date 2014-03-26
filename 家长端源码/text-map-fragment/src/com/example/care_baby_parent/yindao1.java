package com.example.care_baby_parent;

import com.example.text_map_fragment.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class yindao1 extends Activity{
	
	private Button btn;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.yd1);
		SharedPreferences preferences = getSharedPreferences("count",MODE_PRIVATE);
	        int count = preferences.getInt("count", 0);
	        //�жϳ�����ڼ������У�����ǵ�һ����������ת������ҳ��
	        if (count == 0) {
	        	count=1;
	        	Editor editor = preferences.edit();//��ȡ�༭��   
	        	editor.putInt("count", count);
	        	editor.commit();//�ύ�޸�  
	        	btn = (Button)findViewById(R.id.yd_btn1);
	    		btn.setOnClickListener(new View.OnClickListener() { 
	    			  @Override
	    			  public void onClick(View v) {
	    				Intent intent = new Intent();
	    				
	    				intent.setClass(yindao1.this, Yd2.class);
	    				startActivity(intent);
	    				//�����л����������ұ߽��룬����˳�
	    				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_leftxml); 
	    				yindao1.this.finish();
	    				}
	    				});              
	        }
	    	else
	    	{
	    		Intent intent = new Intent();
	    		intent.setClass(getApplicationContext(),login.class);
	    		startActivity(intent);
	    		yindao1.this.finish();
	    	}
		



	}

}
