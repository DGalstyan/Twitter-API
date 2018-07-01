package twiter.ginosi.com.twiterapi.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Davit Galstyan on 6/30/18.
 */
public class Utils {
    /**
     * Is online boolean.
     *
     * @param ctx the ctx
     * @return the boolean
     */
    public static boolean isOnline (Context ctx) {
        final ConnectivityManager connectivity = (ConnectivityManager) ctx.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            final NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (NetworkInfo anInfo : info) {
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
