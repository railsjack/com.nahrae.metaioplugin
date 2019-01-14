package com.nahrae.metaioplugin;

// ADD MAIN .R file ex: import com.libreidee.name.R

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;



import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import java.util.List;



import org.json.JSONArray;



import com.metaio.cloud.plugin.MetaioCloudPlugin;
import com.metaio.cloud.plugin.arel.ARELWebView;
import com.metaio.cloud.plugin.util.MetaioCloudUtils;
import com.metaio.cloud.plugin.view.MetaioCloudARViewActivity;
import com.metaio.sdk.jni.DataSourceEvent;
import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.IMetaioSDKAndroid;
import com.metaio.sdk.jni.LLACoordinate;
import com.metaio.sdk.jni.MetaioWorldPOI;
import com.metaio.sdk.jni.Vector3d;


import com.nahrae.metaioplugin.MetaioCordovaBridge;



import com.nahrae.metaioplugintest.R;


public class MetaioCloudARViewLibreActivity extends MetaioCloudARViewActivity
{

	
	static
	{
		IMetaioSDKAndroid.loadNativeLibs();
	}
    
	/**
	 * GUI overlay
	 */
	private RelativeLayout mGUIView;
	
    
	/**
	 * Progress bar view
	 */
	private ProgressBar progressView;
	
	public MetaioCloudARViewLibreActivity (){}
    
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO Set authentication if a private channel is used
		// MetaioCloudPlugin.setAuthentication("username", "password");
        
		// Ensure that startJunaio is called when this activity is started/recreated to initialize
		// all Cloud Plugin related settings, including setting the correct application identifier.
		// Call synchronously because super.onCreate may already have Cloud Plugin logic. Note
		// that startJunaio is already called in SplashActivity, but in case the application gets
		// restarted, or memory is low, or this activity is started directly without opening
		// SplashActivity, we have to make sure this is always called.
		int result = MetaioCloudPlugin.startJunaio(null, getApplicationContext());
        
		super.onCreate(savedInstanceState);
        
		// Window managed wake lock (no permissions, no accidentally kept on)
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
        
		// Optionaly add GUI
		if (mGUIView == null)
			mGUIView = (RelativeLayout) getLayoutInflater().inflate(R.layout.arview, null);
        
		progressView = (ProgressBar) mGUIView.findViewById(R.id.progressBar);
        
		//Init the AREL webview. Pass a container if you want to use a ViewPager or Horizontal Scroll View over the camera preview or the root view.
		ARELWebView awv = initARELWebView(mGUIView);
		awv.addJavascriptInterface(new WebAppInterface(this), "NativeBridgeInterface");
		
        
		//Used to resume the camera preview
		mIsInLiveMode = true;
		
		if (result != MetaioCloudPlugin.SUCCESS)
			Utils.showErrorForCloudPluginResult(result, this);
			
	}
	
	public class WebAppInterface {
        Context mContext;
        WebAppInterface(Context c) {
            mContext = c;
        }
        @JavascriptInterface
        public String cordovaFunction(String functionName, String args, int callbackId, boolean closeAR)
        {
        	//Log.v("valori", functionName);
        	
        	if((MetaioCordovaBridge.getMethods()).contains(functionName))
        		MetaioCordovaBridge.getPlugin().executeCordovaMethodWithArgs(functionName, args, callbackId);
        	
        	if(closeAR)
        		finish();
        	
        	return functionName;
        }
        @JavascriptInterface
        public String getCordovaMethods()
        {

        	JSONArray methodJSON = new JSONArray(MetaioCordovaBridge.getMethods());	
        	return methodJSON.toString();

        }            
        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        }
    }
    

	
	@Override
	public void onStart() {
		super.onStart();
        
		Utils.log("JunaioARViewTestActivity.onCreate()");
        
		//if we want to catch the sensor listeners
		MetaioCloudPlugin.getSensorsManager(getApplicationContext()).registerCallback(this);
        
		// add GUI layout
		if (mGUIView != null)
			addContentView(mGUIView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        
		//comes from splash activity
		final int channelID = getIntent().getIntExtra(getPackageName() + ".CHANNELID", -1);
		if (channelID > 0) {
			// Clear the intent extra before proceeding
			getIntent().removeExtra(getPackageName() + ".CHANNELID");
			setChannel(channelID);
            
		}
	}
    
	@Override
	protected void onPause() {
		super.onPause();
	}
    
	@Override
	protected void onResume() {
		super.onResume();
	}
    
	@Override
	protected void onStop() {
		super.onStop();
	}
    
	@Override
	public void onLocationSensorChanged(LLACoordinate location) {
		super.onLocationSensorChanged(location);
	}
    
	@Override
	public void onHeadingSensorChanged(float[] orientation) {
		super.onHeadingSensorChanged(orientation);
	}
    
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
    
	@Override
	public void onSurfaceChanged(int width, int height) {
		//always call the super implementation first
		super.onSurfaceChanged(width, height);
        
        Camera camera = IMetaioSDKAndroid.getCamera(this);
        Parameters params = camera.getParameters();
        
        List<String> focusModes = params.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE))
        {
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }
        camera.setParameters(params);
        
		//get radar margins from the resources (this will make the values density independant)
		float marginTop = getResources().getDimension(R.dimen.radarTop);
		float marginRight = getResources().getDimension(R.dimen.radarRight);
		float radarScale = getResources().getDimension(R.dimen.radarScale);
		//set the radar to the top right corner and add some margin, scale to 1
		setRadarProperties(IGeometry.ANCHOR_TOP | IGeometry.ANCHOR_RIGHT, new Vector3d(-marginRight, -marginTop, 0f), new Vector3d(radarScale, radarScale, 1f));
	}
    
	@Override
	protected void updateGUI() {
		// TODO: here update any GUI related to channel information, e.g. icon, name etc
	}
    
	@Override
	public void onScreenshot(Bitmap bitmap) {
		// this is triggered by calling takeScreenshot() or through AREL
		String filename = "junaio-" + DateFormat.format("yyyyMMdd-hhmmss", new Date()) + ".jpg";
        
		try {
			boolean result = MetaioCloudUtils.writeToFile(bitmap, CompressFormat.JPEG, 100, MetaioCloudPlugin.mCacheDir, filename, false);
            
			if (result) {
				if (!saveToGalleryWithoutDialogFlag) {
					//show share view
					Intent intent = new Intent(getApplicationContext(), ShareViewActivity.class);
					intent.putExtra(getPackageName() + ".FILEPATH", MetaioCloudPlugin.mCacheDir + "/" + filename);
					startActivity(intent);
				} else {
					//save the screenshot to the gallery directly
					MetaioCloudUtils.saveScreenshot(bitmap, filename, this);
				}
			}
		} catch (IOException e) {
			Log.e("MetaioCloudARView", "Error formatting date", e);
		} catch (Exception e) {
			Log.e("MetaioCloudARView", "Error formatting date", e);
		}
	}
    
	@Override
	protected void showProgress(final boolean show) {
        
		progressView.post(new Runnable() {
            
			public void run() {
				progressView.setIndeterminate(true);
				progressView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
			}
		});
	}
    
	@Override
	protected void showProgressBar(final float progress, final boolean show) {
        
		progressView.post(new Runnable() {
            
			@Override
			public void run() {
				progressView.setIndeterminate(false);
				progressView.setProgress((int) progress);
				progressView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
                
			}
		});
	}
    
	@Override
	protected void onServerEvent(DataSourceEvent event) {
		switch (event) {
            case DataSourceEventNoPoisReturned:
                MetaioCloudUtils.showToast(this, getString(R.string.MSGI_POIS_NOT_FOUND));
                break;
            case DataSourceEventServerError:
                MetaioCloudUtils.showToast(this, getString(R.string.MSGE_TRY_AGAIN));
                break;
            case DataSourceEventServerNotReachable:
            case DataSourceEventCouldNotResolveServer:
                MetaioCloudUtils.showToast(this, getString(R.string.MSGW_SERVER_UNREACHABLE));
                break;
            default:
                break;
		}
	}
    
	@Override
	protected void onSceneReady() {		
		super.onSceneReady();
		//
		/*
		this.mWebview.setWebViewClient(new WebViewClient(){
			 	@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url)
				{
					 
					String afterDecode = null;
					
					try {
						afterDecode = URLDecoder.decode(url, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					Log.v("callback", afterDecode);		
					

					//WebViewClient client = (WebViewClient) view.;
					//return client.shouldOverrideUrlLoading(view,url);	
					return true;
				}
		});
		*/
		

	}
    
	@Override
	protected void onObjectAdded(MetaioWorldPOI object) {
		super.onObjectAdded(object);
	}
    
	@Override
	protected void onObjectRemoved(MetaioWorldPOI object) {
		super.onObjectRemoved(object);
	}
    
	@Override
	protected void onRemoveContent() {
		super.onRemoveContent();
	}
    
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}


