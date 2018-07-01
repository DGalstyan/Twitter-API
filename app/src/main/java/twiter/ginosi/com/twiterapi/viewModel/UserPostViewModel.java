package twiter.ginosi.com.twiterapi.viewModel;

import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.twitter.sdk.android.core.models.Media;
import com.twitter.sdk.android.core.models.Tweet;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import twiter.ginosi.com.twiterapi.App;
import twiter.ginosi.com.twiterapi.helpers.ImagePickUpUtil;
import twiter.ginosi.com.twiterapi.helpers.States;

/**
 * Created by Davit Galstyan on 6/30/18.
 */
public class UserPostViewModel extends ViewModel {
    private MediatorLiveData<Integer> states;
    private MutableLiveData<Tweet> mTweet;

    public MutableLiveData<Tweet> getCreatedTweet() {
        if (mTweet == null) {
            mTweet = new MutableLiveData();
        }
        return mTweet;
    }

    public void uploadTweet(final String text, final String path) {
        if (path != null) {
            uploadMedia(path, new Callback<Media>() {
                @Override
                public void onResponse(Call<Media> call, Response<Media> response) {
                    uploadTweetWithMedia(text, response.body().mediaIdString);
                }

                @Override
                public void onFailure(Call<Media> call, Throwable t) {
                    states.postValue(States.STATE_ERROR);
                }
            });
        } else {
            uploadTweetWithMedia(text, null);
        }
    }

    private void uploadTweetWithMedia(String text, String mediaId) {
        App.getInstance().getNetworkHttpClient().getStatusesService().update(text, null, null, null, null, null, null, true, mediaId)
            .enqueue(new Callback<Tweet>() {
                @Override
                public void onResponse(Call<Tweet> call, Response<Tweet> response) {
                    mTweet.postValue(response.body());
                }

                @Override
                public void onFailure(Call<Tweet> call, Throwable t) {
                    states.postValue(States.STATE_ERROR);
                }
            });
    }

    private void uploadMedia(String path, Callback<Media> callback) {
        final File file = new File(path);
        final String mimeType = ImagePickUpUtil.getMimeType(file);
        final RequestBody media = RequestBody.create(MediaType.parse(mimeType), file);

        App.getInstance().getNetworkHttpClient().getMediaService().upload(media, null, null).enqueue(callback);
    }

    public MediatorLiveData<Integer> getStates() {
        if (states == null) {
            states = new MediatorLiveData();
        }
        return states;
    }
}
