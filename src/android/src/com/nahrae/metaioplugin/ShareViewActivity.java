/**
 * ShareViewActivity.java
 * Junaio 2.6 Android
 *
 * This activity provides image sharing functionality, e.g.
 * save to external storage, share on facebook etc.
 * 
 * @author Created by Arsalan Malik on 10:36 06.07.2011
 * Copyright 2011 metaio GmbH. All rights reserved.
 *
 */
package com.nahrae.metaioplugin;

// ADD MAIN .R file ex: import com.libreidee.name.R

import java.io.IOException;
import java.util.List;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.nahrae.metaioplugintest.R;



import com.metaio.cloud.plugin.MetaioCloudPlugin;
import com.metaio.cloud.plugin.util.MetaioCloudUtils;

public class ShareViewActivity extends Activity implements MediaScannerConnectionClient, DialogInterface.OnClickListener {

	/**
	 * Screenshot to be shared
	 */
	private ImageView mImageScreenshot;

	/**
	 * Image that is shared
	 */
	private Bitmap mImage;

	/**
	 * Progress Bar
	 */
	private View mProgressBar;

	/**
	 * Media scanner connection
	 */
	private MediaScannerConnection mMediaScanner;

	/**
	 * Image file path
	 */
	private String filepath;

	/**
	 * List of applications that accept pictures to share
	 */
	List<ResolveInfo> list;

	/**
	 * Store the path the first time we save the screenshot
	 */
	String savedPath;

	LinearLayout buttonContainer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.shareviewscreen);


		mProgressBar = findViewById(R.id.progressBar);
		mImageScreenshot = (ImageView) findViewById(R.id.imageScreenshot);
		buttonContainer = (LinearLayout) findViewById(R.id.container);

		mMediaScanner = new MediaScannerConnection(getApplicationContext(), this);

		final String screenshot = getIntent().getStringExtra(getPackageName() + ".FILEPATH");
		if (screenshot != null) {
			mImage = BitmapFactory.decodeFile(screenshot);
			savedPath = screenshot;
		} else {
			finish();
			return;
		}

		//        loadOperations();
		new LoadOps().execute();
	}

	@Override
	protected void onResume() {
		super.onResume();
		try {
			mImageScreenshot.setImageBitmap(mImage);
		} catch (Exception e) {
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		mMediaScanner.disconnect();

		if (mImage != null) {
			MetaioCloudPlugin.log("Recycling bitmap");
			mImage.recycle();
			mImage = null;
		}

		MetaioCloudUtils.unbindDrawables(findViewById(android.R.id.content));
	}

	/**
	 * Show/hide progress bar
	 * 
	 * @param visibility View visibility
	 */
	private void showProgress(final int visibility) {

		final Runnable runnable = new Runnable() {
			@Override
			public void run() {
				mProgressBar.setVisibility(visibility);
			}
		};

		runOnUiThread(runnable);
	}

	/**
	 * Load items in to operations' list
	 */
	private View[] loadOperations() {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SEND);
		intent.setType("image/jpeg");

		final PackageManager packageManager = getPackageManager();
		list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

		View[] buttonsViews = new View[list.size()];

		for (int i = 0, j = list.size(); i < j; i++) {
			ResolveInfo item = list.get(i);
			MetaioCloudPlugin.log(item.activityInfo.packageName);
			final Button actionButton = (Button) getLayoutInflater().inflate(R.layout.button_action_detail, buttonContainer, false);
			actionButton.setText(item.loadLabel(getPackageManager()));
			actionButton.setTag(Integer.valueOf(i));
			actionButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Integer i = (Integer) v.getTag();
					ResolveInfo info = list.get(i);
					Intent intent = new Intent(Intent.ACTION_SEND);
					intent.setClassName(info.activityInfo.packageName, info.activityInfo.name);
					intent.setType("image/jpeg");
					if (savedPath == null) {
						savedPath = saveScreenshot(mImage);
					}
					if (savedPath != null) {
						intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + savedPath));
						startActivity(intent);
					}
				}
			});

			buttonsViews[i] = actionButton;
		}

		return buttonsViews;
	}

	class LoadOps extends AsyncTask<Void, Void, View[]> {

		@Override
		protected void onPreExecute() {
			mProgressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected View[] doInBackground(Void... params) {
			return loadOperations();
		}

		@Override
		protected void onPostExecute(View[] actionButton) {
			for (View v : actionButton)
				buttonContainer.addView(v);

			mProgressBar.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (which == DialogInterface.BUTTON_POSITIVE) {
			showProgress(View.VISIBLE);
		}
	}

	public void onSaveClick(View v) {
		saveScreenshot(mImage);
	}

	private String saveScreenshot(Bitmap image) {
		String path = null;
		try {
			mMediaScanner.connect();
			path = MetaioCloudUtils.saveScreenshot(mImage, filepath, this);
			Log.i("ShareViewActivity", String.format("Image saved at %s", path));
		} catch (IOException e) {
			Log.e("ShareViewActivity", "Saving failed", e);
		} catch (Exception e) {
			Log.e("ShareViewActivity", "Saving failed", e);
		}

		return path;
	}

	@Override
	public void onMediaScannerConnected() {
		mMediaScanner.scanFile(filepath, null);
	}

	@Override
	public void onScanCompleted(String path, Uri uri) {
		mMediaScanner.disconnect();
	}

}
