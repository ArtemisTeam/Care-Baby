package com.example.care_baby_parent;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import com.example.text_map_fragment.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.support.v4.app.Fragment;

public class DIYMenu extends Fragment {
	private ListView lv=null;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.zxc, null);
		lv=(ListView) v.findViewById(R.id.listView1);
		String[] colors = getResources().getStringArray(R.array.color_names);
		ArrayAdapter<String> colorAdapter = new ArrayAdapter<String>(getActivity(), 
				android.R.layout.simple_list_item_1, android.R.id.text1, colors);
		
		SimpleAdapter adapter = new SimpleAdapter(getActivity(),getData(),R.layout.vlist,
				new String[]{"title","info","img"},
				new int[]{R.id.title,R.id.info,R.id.img});
		
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long id) {
				Fragment newContent = null;
				switch (position) {
				case 0:
					newContent = new MapFragment();
					break;
				case 1:
					newContent = new talk();
					break;
				case 2:
					newContent = new SMSFragment();
					break;
				case 3:
					newContent = new phoneFragment();
					break;
				case 4:
					int login_style=0;
					SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userInfo",Context.MODE_PRIVATE);   
		            Editor editor = sharedPreferences.edit();//获取编辑器   
		            editor.putInt("login_style", login_style); 
		            editor.commit();//提交修改   
		            Toast.makeText(getActivity(),"成功注销" , Toast.LENGTH_LONG).show();
		            getActivity().finish();
					break;

				}
				if (newContent != null)
					switchFragment(newContent);
			}
		});


		return v;
		
	}
	
	private List<Map<String, Object>> getData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Map<String, Object> map = new HashMap<String, Object>();
		//map.put("title", "    地图");
//		map.put("info","asd");
		map.put("img", R.drawable.dingwei);
		list.add(map);

		map = new HashMap<String, Object>();
//		map.put("title", "    聊天");
		map.put("img", R.drawable.liantian);
		list.add(map);

		map = new HashMap<String, Object>();
//		map.put("title", "    短信");
		map.put("img", R.drawable.duanxin);
		list.add(map);
		
		map = new HashMap<String, Object>();
//		map.put("title", "    通话");
		map.put("img", R.drawable.tonghua);
		list.add(map);
		
		map = new HashMap<String, Object>();

		map = new HashMap<String, Object>();
//		map.put("title", "    注销");
		map.put("img", R.drawable.zhuxiao);
		list.add(map);
		
		return list;
	}
	private void switchFragment(Fragment fragment) {
		if (getActivity() == null)
			return;
		
		if (getActivity() instanceof MainActivity) {
			MainActivity fca = (MainActivity) getActivity();
			fca.switchContent(fragment);
		} 
	}


}
