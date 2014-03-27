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

    //�㲥��Ϣ����
    public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
    //����onReceive����
    @Override
    public void onReceive(Context context, Intent intent) 
    {
    	Log.i("xiangying","1");
		
        // TODO Auto-generated method stub
        Log.i(TAG, "���������¼�");
        //StringBuilder body=new StringBuilder("");//��������
        //StringBuilder sender=new StringBuilder("");//������
        SharedPreferences preferences = context.getSharedPreferences("userInfo",Context.MODE_PRIVATE);   
        username = preferences.getString("username", "");  

        //���жϹ㲥��Ϣ
        String action = intent.getAction();
        if (SMS_RECEIVED_ACTION.equals(action))
        {
            //��ȡintent����
        	Bundle bundle=intent.getExtras();
            //�ж�bundle����
            if (bundle!=null)
            {
                //ȡpdus����,ת��ΪObject[]
                Object[] pdus=(Object[])bundle.get("pdus");
                //��������
                SmsMessage[] messages = new SmsMessage[pdus.length];
                for(int i=0;i<messages.length;i++)
                {
                    byte[] pdu=(byte[])pdus[i];
                    messages[i]=SmsMessage.createFromPdu(pdu);
                }    
                //���������ݺ�����������
                for(SmsMessage msg:messages)
                {
                    //��ȡ��������
                    String content=msg.getMessageBody();
                    System.out.println(content);
                    Log.i("content", content);
                    //������
                    String sender=msg.getOriginatingAddress();
                    Log.i("sender", sender);
                    System.out.println(sender);
                    //ʱ��
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
                    		//avo.put("sender_name", sendname);//��ϵ������(İ���˾�дİ����)
                    		avo.put("content",content);
                    		avo.saveInBackground();
                    		Log.i(sender,content);
                    		Toast.makeText(context, "�յ�:"+sender+"����:"+content+"ʱ��:"+sendTime.toString(), Toast.LENGTH_LONG).show();
                    	
                    }
                }
                
            }
        }
    }

}
