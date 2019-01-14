package com.nahrae.metaioplugin;

import java.util.ArrayList;
import java.util.List;

import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.nahrae.metaioplugin.MetaioCloud;


public class MetaioCordovaBridge {
    private static MetaioCordovaBridge instance = null;
    private static Context context;
    private static List<String> _methods = new ArrayList<String>();
    private static MetaioCloud plugin;

    /**
     * To initialize the class. It must be called before call the method getInstance()
     * @param ctx The Context used

     */
    public static void initialize(Context ctx,MetaioCloud plugin,List<String> methods) {
     context = ctx;
     setMethods(methods);
     setPlugin(plugin);
     
    }

    /**
     * Check if the class has been initialized
     * @return true  if the class has been initialized
     *         false Otherwise
     */
    public static boolean hasBeenInitialized() {
     return context != null;

    }

    /**
    * The private constructor. Here you can use the context to initialize your variables.
    */
    private MetaioCordovaBridge() {
        // Use context to initialize the variables.
    }

    /**
    * The main method used to get the instance
    */
    public static synchronized MetaioCordovaBridge getInstance() {
     if (context == null) {
      throw new IllegalArgumentException("Impossible to get the instance. This class must be initialized before");
     }

     if (instance == null) {
      instance = new MetaioCordovaBridge();
     }

     return instance;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Clone is not allowed.");
    }

	public static List<String> getMethods() {
		return _methods;
	}

	public static void setMethods(List<String> methods) {
		MetaioCordovaBridge._methods = methods;
	}


	public static MetaioCloud getPlugin() {
		return plugin;
	}

	public static void setPlugin(MetaioCloud plugin) {
		MetaioCordovaBridge.plugin = plugin;
	}


	
	
}
