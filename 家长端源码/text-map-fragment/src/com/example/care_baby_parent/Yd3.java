package com.example.care_baby_parent;

import com.example.text_map_fragment.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Yd3 extends Activity{
	
	private Button btn;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.yd3);
		btn = (Button)findViewById(R.id.yd_btn3);
		btn.setOnClickListener(new View.OnClickListener() { 
			  @Override
			  public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(Yd3.this, Yd4.class);
				startActivity(intent);
				//设置切换动画，从右边进入，左边退出
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_leftxml);     
				Yd3.this.finish();
             }
		  });

	}

}

