package com.nahrae.metaioplugin;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.metaio.cloud.plugin.view.ARMetaioPluginManager;
import com.metaio.sdk.MetaioWorldPOIManagerCallback;

/**
 * Base class for Metaio Cloud Plugin apps. Change the parent class of this class to provide a
 * different kind of Activity like ActionBarActivity, FragmentActivity, MapActivity, etc.
 */
public abstract class MetaioCloudPluginBaseActivity extends FragmentActivity
{

	protected ARMetaioPluginManager mMetaioPluginManager;
	protected MetaioWorldPOIManagerCallback mMetaioWorldPOIManagerCallback;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// get the ARMetaioPluginManager instance of the children if any, if not, create a
		// default one
		mMetaioPluginManager = getARMetaioPluginManagerInstance();
		if (mMetaioPluginManager == null)
			mMetaioPluginManager = new ARMetaioPluginManager(this);
		mMetaioPluginManager.onActivityCreated();
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		// Next line registers sensor callbacks, creates the views and inits metaioSDK
		mMetaioPluginManager.onActivityStarted();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		// resumes sensors, resume surfaceview if mIsInLiveMode is true
		mMetaioPluginManager.onActivityResumed();
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		// pauses sensors, unregisters sensors callback, cancels requests, pauses surfaceview
		mMetaioPluginManager.onActivityPaused();
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		// stops sensors and movie textures, removes surface view, stops camera
		mMetaioPluginManager.onActivityStopped();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		// cleans up Metaio SDK
		mMetaioPluginManager.onActivityDestroyed();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		mMetaioPluginManager.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		mMetaioPluginManager.onActivityConfigurationChanged(newConfig);
	}

	/**
	 * Override this to provide your own implementation
	 * 
	 * @return a class that extends {@link ARMetaioPluginManager} which provides custom
	 *         implementation of some of the methods
	 */
	public abstract ARMetaioPluginManager getARMetaioPluginManagerInstance();

	/**
	 * Override this to provide your own implementation
	 * 
	 * @return a class that extends {@link MetaioWorldPOIManagerCallback} which provides custom
	 *         implementation of some of the methods
	 */
	public abstract MetaioWorldPOIManagerCallback getMetaioWorldPOIManagerCallback();

}
