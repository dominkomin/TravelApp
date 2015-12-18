package dk.itu.mmad.travelApp.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import dk.itu.mmad.travelApp.R;

public class SMSReceiver extends BroadcastReceiver
{
	public SMSReceiver()
	{
	}

	@Override
	public void onReceive(Context context, Intent intent)
	{
		Bundle extras = intent.getExtras();
		if (extras != null) {
			Object[] pdus = (Object[]) extras.get("pdus");
			SmsMessage message = SmsMessage.createFromPdu((byte[]) pdus[0]);
			String sender = message.getOriginatingAddress();
			String origMessage = message.getMessageBody();
			String smsContent = context.getResources().getString(R.string.SmsInviteNotify);
			if (origMessage.equals(smsContent))
			{
				abortBroadcast();
				Toast.makeText(context, sender + " has installed the great app!", Toast.LENGTH_LONG).show();
			}
		}
	}
}
