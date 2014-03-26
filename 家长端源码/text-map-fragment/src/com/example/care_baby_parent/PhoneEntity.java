package com.example.care_baby_parent;

public class PhoneEntity {
	private String name;//消息来自
	private String date;//消息日期
	private String number;//消息内容
	private boolean isComMeg = true;// 是否为收到的消息

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getMessage() {
		return number;
	}

	public void setMessage(String message) {
		this.number = message;
	}

	public boolean getMsgType() {
		return isComMeg;
	}

	public void setMsgType(boolean isComMsg) {
		isComMeg = isComMsg;
	}

	public PhoneEntity() {
	}

	public PhoneEntity(String name, String date, String text, boolean isComMsg) {
		super();
		this.name = name;
		this.date = date;
		this.number = text;
		this.isComMeg = isComMsg;
	}


}
