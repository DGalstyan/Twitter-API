package twiter.ginosi.com.twiterapi.network;

/**
 * Created by Davit Galstyan on 6/30/18.
 */
public interface NetworkReceiverListener {
    /**
     * Network available.
     */
    void networkAvailable();

    /**
     * Network unavailable.
     */
    void networkUnavailable();
}
