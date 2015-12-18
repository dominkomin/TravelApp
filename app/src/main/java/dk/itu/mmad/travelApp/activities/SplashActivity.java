package dk.itu.mmad.travelApp.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import dk.itu.mmad.travelApp.adapters.PagerAdapter;
import dk.itu.mmad.travelApp.R;
import dk.itu.mmad.travelApp.fragments.SplashFragment;
import dk.itu.mmad.travelApp.fragments.VideoFragment;

public class SplashActivity extends FragmentActivity
{

	private static final int STOP_SPLASH = 0;
	private Handler splashHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				case STOP_SPLASH:
					finish();
					break;
			}
			super.handleMessage(msg);
		}
	};
	private static final long SPLASH_TIME = 10000;
	private boolean firstTime = true;
	private ImageView splash;

	@Override
	public void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		setContentView(R.layout.splashscreen);

		Button skipButton = (Button) findViewById(R.id.skipButton);
		skipButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});

		final ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
		final VideoFragment videoFragment = new VideoFragment();
		final SplashFragment splashFragment = new SplashFragment();

		PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());
		pagerAdapter.addFragment(splashFragment);
		pagerAdapter.addFragment(videoFragment);
		viewPager.setAdapter(pagerAdapter);

		final Button playButton = (Button) findViewById(R.id.playButton);
		playButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (firstTime)
				{
					firstTime = false;
					splashHandler.removeMessages(STOP_SPLASH);
					viewPager.setCurrentItem(1);
					videoFragment.play();
					playButton.setVisibility(View.GONE);
				}
			}
		});

		Message msg = new Message();
		msg.what = STOP_SPLASH;
		splashHandler.sendMessageDelayed(msg, SPLASH_TIME);

	}
}