package com.example.care_baby_parent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class menuFragment extends ListFragment {

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
		Activity n1 = null;
		switch (position) {
		case 0:
			newContent = new map(R.color.red);
			break;
		case 1:
			newContent = new talk();
			break;
		case 2:
			newContent = new map(R.color.blue);
			break;
		case 3:
			newContent = new map(android.R.color.white);
			break;
		case 4:
			newContent = new map(android.R.color.black);
			break;
		}
		if (newContent != null)
			switchFragment(newContent);
	}

	// «–ªªFragment ”Õºƒ⁄ring
	private void switchFragment(Fragment frag) {
		if (getActivity() == null)
			return;
		
		if (getActivity() instanceof fragment) {
			fragment fca = (fragment) getActivity();
			fca.switchContent(frag);
		} 
	}


}
