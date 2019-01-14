package com.nahrae.metaioplugin;


import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaActivity;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;


import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.os.Handler;
import android.os.Vibrator;
import android.sax.StartElementListener;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.nahrae.metaioplugin.MetaioCordovaBridge;

/**
 * This class provides access to vibration on the device.
 */
public class MetaioCloud extends CordovaPlugin {
	
    private CallbackContext callbackContext;
	public Handler handler;
    /**
     * Constructor.
     */
    public MetaioCloud() {
    }
    
    static final int PICK_CONTACT_REQUEST = 0;
    
    @Override
    public boolean execute(String action, final JSONArray args, CallbackContext callbackContext) {
        this.callbackContext = callbackContext;
               
        if(action.equals("metaiocloud")){
        	cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                	
                	final int prova = args.optInt(0);
                	
                	List<String> methods = new ArrayList<String>();
                	
                	for (int i=1; i < args.length(); i++)
                	{
                		methods.add(args.optString(i));
                	}
                	               	
                	               	
                	                	
                	Log.v("numero che passa","NUMERO: " +prova);
                	
                    Context context = cordova.getActivity().getApplicationContext();
                    Intent intent = new Intent(context,MetaioCloudARViewLibreActivity.class);
                    
                    intent.putExtra(cordova.getActivity().getPackageName() + ".CHANNELID", prova);
                    
                	MetaioCordovaBridge.initialize(context, MetaioCloud.this, methods); 
                	
                	webView.addJavascriptInterface(new CordovaInterface(context), "CordovaJSInterface");               	
                	
                	
                    
                    cordova.setActivityResultCallback(MetaioCloud.this); // setto questo come responder del risultato della activity
                    cordova.getActivity().startActivityForResult(intent,PICK_CONTACT_REQUEST);
                    
                }
            });
        	return true;
        }
        return false;
    }
    

    
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    	Log.v("risultato", "torna!");
        if (requestCode == PICK_CONTACT_REQUEST) {
            if (resultCode == 0) {
                
            }
        }
    }
    
	class CordovaInterface {
        Context mContext;
        CordovaInterface(Context c) {
            mContext = c;
        }     
        
        @JavascriptInterface
        public void returnJSValues(String returnValues) { //TODO non so se javascript ritornaa sempre stringhe!
            //Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        	Log.v("ecco il risultato!", returnValues);
        }
    }
	
    public  void executeCordovaMethodWithArgs(String methodName, String args, int callbackId)
	{
    	

    	final String jsFunction = String.format("javascript:navigator.metaio.executeMethod('%s',['%s'])",methodName,args);
		Log.v("funzione",methodName);
		Log.v("funzione chiamata",jsFunction);
    	
    	cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
        		webView.loadUrl(jsFunction);
            }
    	});
	}
    
    public void launchMetaio(){
    	//Intent myIntent = new Intent("com.libreidee.metaiocloud.SplashActivity");
    	//myIntent.addCategory(Intent.CATEGORY_DEFAULT);
    	
    	Context context=this.cordova.getActivity().getApplicationContext();
        //or Context context=cordova.getActivity().getApplicationContext();
        Intent intent=new Intent(context, MetaioCloudARViewLibreActivity.class);
        context.startActivity(intent);
    	//Intent intent = new Intent(cordova.getActivity(), MetaioCloudARViewLibreActivity.class);
		//intent.putExtra(getPackageName() + ".CHANNELID", channelId);
		//startActivity(intent);
    	
    	//this.cordova.startActivityForResult(arg0, arg1, arg2)
        //this.cordova.startActivityForResult((CordovaPlugin) this, intent,123232);
    }
}
