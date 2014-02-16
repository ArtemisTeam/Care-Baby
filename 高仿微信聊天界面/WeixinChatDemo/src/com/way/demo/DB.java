package com.way.demo;

import java.text.SimpleDateFormat;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DB {
	private SQLiteDatabase db=null;
	private SimpleDateFormat sDateFormat=null;
	public DB()
	{
		db.openOrCreateDatabase("care_baby.db", null);  
		Cursor cnt= db.rawQuery("select count(*) as num from sqlite_master where type= 'table' and name = ?",new String[]{"Chat_Message"});
		if(cnt.moveToFirst()&&(cnt.getInt(cnt.getColumnIndex("num"))==0)){
			db.execSQL("CREATE TABLE Chat_Message (_id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT, message TEXT,tm TEXT)");  
		}
		cnt.close();
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	}
	public void InsertMsg(String name,String msg)
	{
		String date = sDateFormat.format(new java.util.Date());
		db.execSQL("INSERT INTO Chat_Message VALUES (null,?, ?, ?)", new Object[]{name, msg,date});  
	}
	public void InsertMsg(String name,String msg,String date)
	{
		db.execSQL("INSERT INTO Chat_Message VALUES (null,?, ?, ?)", new Object[]{name, msg,date}); 
	}
	public void CloseDB()
	{
		db.close();
	}

}
