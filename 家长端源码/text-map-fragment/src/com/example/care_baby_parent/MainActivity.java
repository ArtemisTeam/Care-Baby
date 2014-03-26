package com.example.care_baby_parent;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;


import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.PushService;
import com.example.text_map_fragment.R;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class MainActivity extends SlidingFragmentActivity {

	private Fragment mContent;
	String username=null;
	String password=null;
	
	String message=null;
	String date=null;

	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyApplication myapplication = new MyApplication();
		setContentView(R.layout.start);
		AVOSCloud.useAVCloudCN();
		AVOSCloud.initialize(this, "9f9kc7n2gfm4ug07arijhef5roy4kvq4l1xh6voy714hfswm", "2nuzsw9x0tw3mheybe7spewu43bfwnfoujry68g51973watr");
//		AVAnalytics.trackAppOpened(getIntent());
		PushService.setDefaultPushCallback(this, MainActivity.class);
		setTitle("功能");
		initSlidingMenu(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	/**
	 * 初始化滑动菜�?
	 */

	
	private void initSlidingMenu(Bundle savedInstanceState){	
		//如果保存的状态不为空则得到ColorFragment，否则实例化ColorFragment
		if (savedInstanceState != null)
			mContent = getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
		if (mContent == null)
			mContent = new StartFragment();	
		
		// 设置主视图界�?
		setContentView(R.layout.content_frame);
		getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mContent).commit();

		// 设置滑动菜单视图界面
		setBehindContentView(R.layout.menu_frame);
		getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame, new DIYMenu()).commit();

		// 设置滑动菜单的属性�?
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		getSlidingMenu().setShadowWidthRes(R.dimen.shadow_width);	
		getSlidingMenu().setShadowDrawable(R.drawable.shadow);
		getSlidingMenu().setBehindOffsetRes(R.dimen.slidingmenu_offset);
		getSlidingMenu().setFadeDegree(0.35f);
		
	}
	
	/**
	 * 切换Fragment，也是切换视图的内容
	 */
	public void switchContent(Fragment fragment) {
		mContent = fragment;
		getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
		getSlidingMenu().showContent();
	}

	/**
	 * 菜单按钮点击事件，�?过点击ActionBar的Home图标按钮来打�?��动菜�?
	 */
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			toggle();
			return true;	
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * 保存Fragment的状�?
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState, "mContent", mContent);
	}
	
}
