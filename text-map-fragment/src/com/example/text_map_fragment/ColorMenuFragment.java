package com.example.text_map_fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/*
 * ����ȱ�ٵ������⵼��bug
 */
public class ColorMenuFragment extends ListFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.list, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		String[] colors = getResources().getStringArray(R.array.color_names);
		
		ArrayAdapter<String> colorAdapter = new ArrayAdapter<String>(getActivity(), 
				android.R.layout.simple_list_item_1, android.R.id.text1, colors);
		
		setListAdapter(colorAdapter);
	}

	@Override
	public void onListItemClick(ListView lv, View v, int position, long id) {
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
			int login_style=0;
			SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userInfo",Context.MODE_PRIVATE);   
            Editor editor = sharedPreferences.edit();//��ȡ�༭��   
            editor.putInt("login_style", login_style); 
            editor.commit();//�ύ�޸�   
            Toast.makeText(getActivity(),"�ɹ�ע��" , Toast.LENGTH_LONG).show();
            getActivity().finish();
			break;
		}
		if (newContent != null)
			switchFragment(newContent);
	}

	// �л�Fragment��ͼ��ring
	private void switchFragment(Fragment fragment) {
		if (getActivity() == null)
			return;
		
		if (getActivity() instanceof MainActivity) {
			MainActivity fca = (MainActivity) getActivity();
			fca.switchContent(fragment);
		} 
	}


}
