package com.example.care_baby_parent;

import com.example.text_map_fragment.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Yd4 extends Activity{
	
	private Button btn;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.yd4);
		btn = (Button)findViewById(R.id.yd_btn4);
		btn.setOnClickListener(new View.OnClickListener() { 
			  @Override
			  public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(Yd4.this, login.class);
				startActivity(intent);
				//�����л����������ұ߽��룬����˳�
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_leftxml);   
				Yd4.this.finish();
             }
		  });

	}

}

