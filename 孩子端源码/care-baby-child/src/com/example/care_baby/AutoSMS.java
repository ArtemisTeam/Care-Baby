package com.example.care_baby;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.avos.avoscloud.AVObject;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.SmsMessage;
import android.util.Log;

import android.widget.Toast;

public class AutoSMS extends BroadcastReceiver 
{


    private String TAG="AutSMS";
    String username=null;	

    //广播消息类型
    public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
    //覆盖onReceive方法
    @Override
    public void onReceive(Context context, Intent intent) 
    {
    	Log.i("xiangying","1");
		
        // TODO Auto-generated method stub
        Log.i(TAG, "引发接收事件");
        //StringBuilder body=new StringBuilder("");//短信内容
        //StringBuilder sender=new StringBuilder("");//发件人
        SharedPreferences preferences = context.getSharedPreferences("userInfo",Context.MODE_PRIVATE);   
        username = preferences.getString("username", "");  

        //先判断广播消息
        String action = intent.getAction();
        if (SMS_RECEIVED_ACTION.equals(action))
        {
            //获取intent参数
        	Bundle bundle=intent.getExtras();
            //判断bundle内容
            if (bundle!=null)
            {
                //取pdus内容,转换为Object[]
                Object[] pdus=(Object[])bundle.get("pdus");
                //解析短信
                SmsMessage[] messages = new SmsMessage[pdus.length];
                for(int i=0;i<messages.length;i++)
                {
                    byte[] pdu=(byte[])pdus[i];
                    messages[i]=SmsMessage.createFromPdu(pdu);
                }    
                //解析完内容后分析具体参数
                for(SmsMessage msg:messages)
                {
                    //获取短信内容
                    String content=msg.getMessageBody();
                    System.out.println(content);
                    Log.i("content", content);
                    //发送者
                    String sender=msg.getOriginatingAddress();
                    Log.i("sender", sender);
                    System.out.println(sender);
                    //时间
                    Date date = new Date(msg.getTimestampMillis());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String sendTime = sdf.format(date);
                    if(content.length()>=6)
                    {
                    	String message=content.substring(0,5);
                    	Log.i(username,content);

                    		AVObject avo=new AVObject("SMS");
                    		avo.put("username",username);
                    		avo.put("sender", sender);
                    		//String sendname=getPeopleNameFromPerson(sender);
                    		//avo.put("sender_name", sendname);//联系人姓名(陌生人就写陌生人)
                    		avo.put("content",content);
                    		avo.saveInBackground();
                    		Log.i(sender,content);
                    		Toast.makeText(context, "收到:"+sender+"内容:"+content+"时间:"+sendTime.toString(), Toast.LENGTH_LONG).show();
                    	
                    }
                }
                
            }
        }
    }

}
