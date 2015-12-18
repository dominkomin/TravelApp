package dk.itu.mmad.travelApp.fragments;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import dk.itu.mmad.travelApp.R;

/**
 * Created by domi on 26-04-2015.
 */
public class SplashFragment extends Fragment
{
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		ImageView image=(ImageView)getActivity().findViewById(R.id.splashTrain);

		RotateAnimation anim = new RotateAnimation(0f, 350f, 15f, 15f);
		anim.setInterpolator(new LinearInterpolator());
		anim.setRepeatCount(Animation.INFINITE);
		anim.setDuration(1000);
		anim.setRepeatCount(1);

		image.startAnimation(anim);
		AnimatorSet fadeInAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(), R.animator.fade_in);

		fadeInAnimator.setTarget(image);
		fadeInAnimator.start();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.splash_fragment, container, false);
	}

}
