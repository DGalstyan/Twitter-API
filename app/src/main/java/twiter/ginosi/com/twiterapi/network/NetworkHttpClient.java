package twiter.ginosi.com.twiterapi.network;

import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.services.StatusesService;

/**
 * Created by Davit Galstyan on 6/30/18.
 */
public class NetworkHttpClient extends TwitterApiClient {
    private String userName;
    private Long userId;


    public NetworkHttpClient(TwitterSession session) {
        super(session);
        userId = session.getUserId();
        userName= session.getUserName();
    }

    /**
     * Provide CustomService with defined endpoints
     */
    public TwiterAPI getCustomService() {
        return getService(TwiterAPI.class);
    }

    public Long getUserId() {
        return this.userId;
    }

    public StatusesService getStatusesService() {
        return getService(StatusesService.class);
    }

    public String getUserName() {
        return this.userName;
    }
}