package dk.itu.mmad.travelApp.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import dk.itu.mmad.travelApp.db.TravelDAO;

/**
 * Created by domi on 23-03-2015.
 *
 * I have chosen just a plain service since it gives me an ability to group all automatic check-out flow in a one place.
 * Notification to app is realized by a subscriber-broadcaster. Travel saving and toasts notifications are included in the service.
 *
 * Is it a correct approach?
 */
public class CheckInOutService extends Service
{
	public static final String CHECK_IN_STATION = "checkInStation";
	public static final String AUTOMATIC_CHECK_OUT = "automaticCheckOut";

	final Handler handler = new Handler();

	Timer timer;
	TravelDAO travelDAO;


	public CheckInOutService()
	{
		super();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		final String checkIn = intent.getExtras().getString(CHECK_IN_STATION);

		timer = new Timer();
		TimerTask notifyToBeExpired = new TimerTask()
		{
			public void run()
			{
				handler.post(new Runnable()
				{
					public void run()
					{
						Toast.makeText(CheckInOutService.this, "Ticket is going to be expired", Toast.LENGTH_LONG).show();
					}
				});
			}
		};
		TimerTask notifyExpired = new TimerTask()
		{
			public void run()
			{
				travelDAO = new TravelDAO(CheckInOutService.this);
				travelDAO.open();

				travelDAO.saveTravel(checkIn, "Unknown station", 0);

				Intent automaticCheckOut = new Intent(AUTOMATIC_CHECK_OUT);
				sendBroadcast(automaticCheckOut);

				handler.post(new Runnable()
				{
					public void run()
					{
						Toast.makeText(CheckInOutService.this, "Ticket expired. You have been checked out.", Toast.LENGTH_LONG).show();
					}
				});

				stopSelf();
			}
		};

		timer.schedule(notifyToBeExpired, 1000000);
		timer.schedule(notifyExpired, 1500000);

		return Service.START_REDELIVER_INTENT;
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if (timer != null)
		{
			timer.cancel();
		}
		if (travelDAO != null)
		{
			travelDAO.close();
		}
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}
}
