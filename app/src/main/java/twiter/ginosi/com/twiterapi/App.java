package twiter.ginosi.com.twiterapi;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterSession;

import twiter.ginosi.com.twiterapi.helpers.Bus;
import twiter.ginosi.com.twiterapi.network.NetworkHttpClient;

/**
 * Created by Davit Galstyan on 6/30/18.
 */
public class App extends Application {
    private static App app;
    private NetworkHttpClient customApiClient;

    public static App getInstance() {
        return app;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        Fresco.initialize(this);
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(getResources().getString(R.string.twitter_key), getResources().getString(R.string.twitter_secret)))
                .debug(true)
                .build();
        Twitter.initialize(config);
    }

    public void initClient(TwitterSession twitterSession){
        if(customApiClient== null) {
            customApiClient = new NetworkHttpClient(twitterSession);
        }
    }

    public NetworkHttpClient getNetworkHttpClient(){
        return customApiClient;
    }

    public Bus getBus() {
        return Bus.getInstance();
    }


}
