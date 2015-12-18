package dk.itu.mmad.travelApp.fragments;


import android.app.Fragment;
import android.app.ListFragment;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import dk.itu.mmad.travelApp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class InviteFragment extends ListFragment
{
	public InviteFragment()
	{
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_checked, data);
		//setListAdapter(adapter);

		Cursor cursor = getActivity().getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null,
				null, null, ContactsContract.Contacts.DISPLAY_NAME);

		String[] from = {ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts._ID};
		int[] to = {android.R.id.text1, android.R.id.text2};
		SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(getActivity(),
				android.R.layout.simple_list_item_2, cursor, from, to, 0);
		setListAdapter(cursorAdapter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_invite, container, false);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		super.onListItemClick(l, v, position, id);

		Cursor cursor = (Cursor) l.getItemAtPosition(position);
		String hasPhone = cursor.getString(cursor
				.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER));
		if (Integer.parseInt(hasPhone) == 1)
		{
			String personId = cursor.getString(cursor
					.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
			Cursor personCursor = getActivity().getContentResolver().query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = "
							+
							personId, null, null);
			List<String> phoneNumbers = new ArrayList<String>();
			while (personCursor.moveToNext())
				phoneNumbers.add(personCursor.getString(personCursor.getColumnIndex( ContactsContract.CommonDataKinds.Phone.NUMBER)));

			SmsManager smsManager = SmsManager.getDefault();
			PendingIntent pendingIntent  = PendingIntent.getBroadcast(getActivity(), 0, new Intent("SMS_SENT"), 0);
			smsManager.sendTextMessage(phoneNumbers.get(0), null, getResources().getString(R.string.SmsInvite), pendingIntent, null);

			ContentValues values = new ContentValues();
			values.put("address", phoneNumbers.get(0));
			values.put("body", getResources().getString(R.string.SmsInvite));
			getActivity().getContentResolver().insert(Uri.parse("content://sms/sent"), values);

			Toast.makeText(getActivity(), "Invite has been sent.", Toast.LENGTH_LONG).show();
		}
		else
			Toast.makeText(getActivity(), "Contact does not have any phone number defined.", Toast.LENGTH_LONG).show();
	}
}
