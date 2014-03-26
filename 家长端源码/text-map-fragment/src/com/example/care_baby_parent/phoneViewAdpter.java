package com.example.care_baby_parent;

import java.util.List;

import com.example.text_map_fragment.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;



public class phoneViewAdpter extends BaseAdapter  {

	public static interface IMsgViewType {
		int IMVT_COM_MSG = 0;// 收到对方的消�?
		int IMVT_TO_MSG = 1;// 自己发�?出去的消�?
	}
	
	private static final int ITEMCOUNT = 2;// 消息类型的�?�?
	private List<PhoneEntity> coll;// 消息对象数组
	private LayoutInflater mInflater;
	
	public  phoneViewAdpter(Context context, List<PhoneEntity> coll) {
		this.coll = coll;
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return coll.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	/**
	 * 得到Item的类型，是对方发过来的消息，还是自己发�?出去�?
	 */
	@Override
	public int getItemViewType(int position) {
		PhoneEntity entity = coll.get(position);

		if (entity.getMsgType()) {//收到的消�?
			return IMsgViewType.IMVT_COM_MSG;
		} else {//自己发�?的消�?
			return IMsgViewType.IMVT_TO_MSG;
		}
	}
	
	@Override
	public int getViewTypeCount() {
		return ITEMCOUNT;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		PhoneEntity entity = coll.get(position);
		boolean isComMsg = entity.getMsgType();

		ViewHolder viewHolder = null;
		if (convertView == null) {

			convertView = mInflater.inflate(
						R.layout.smsitem, null);
			viewHolder = new ViewHolder();
			viewHolder.tvSendTime = (TextView) convertView
					.findViewById(R.id.tv_sendtime);
			viewHolder.tvUserName = (TextView) convertView
					.findViewById(R.id.textView1);
			viewHolder.tvContent = (TextView) convertView
					.findViewById(R.id.tv_chatcontent);
			viewHolder.isComMsg = isComMsg;

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.tvSendTime.setText(entity.getDate());
		viewHolder.tvUserName.setText(entity.getName());
		viewHolder.tvContent.setText(entity.getMessage());
		return convertView;
	}

	static class ViewHolder {
		public TextView tvSendTime;
		public TextView tvUserName;
		public TextView tvContent;
		public boolean isComMsg = true;
	}

}
