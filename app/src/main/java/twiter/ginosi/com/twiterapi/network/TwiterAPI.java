package twiter.ginosi.com.twiterapi.network;

import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Davit Galstyan on 6/30/18.
 */
public interface TwiterAPI {

    @GET("/1.1/users/show.json")
    Call<User> show(@Query("user_id") long id);
}
