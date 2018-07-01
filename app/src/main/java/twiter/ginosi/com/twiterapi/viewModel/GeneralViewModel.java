package twiter.ginosi.com.twiterapi.viewModel;

import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import twiter.ginosi.com.twiterapi.App;
import twiter.ginosi.com.twiterapi.helpers.States;
import twiter.ginosi.com.twiterapi.network.NetworkHttpClient;


/**
 * Created by Davit Galstyan on 6/30/18.
 */
public class GeneralViewModel extends ViewModel {

    private MediatorLiveData<Integer> states;
    // Create a LiveData with a TwitterSession
    private MutableLiveData<TwitterSession> mTwitterSession;



    public void setUpTwitterButton(TwitterLoginButton twitterButton) {
        twitterButton.setCallback(new com.twitter.sdk.android.core.Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                mTwitterSession.postValue(result.data);
            }

            @Override
            public void failure(TwitterException exception) {
                states.postValue(States.STATE_ERROR);
            }
        });
    }

    public MutableLiveData<TwitterSession> getTwitterSession() {
        if (mTwitterSession == null) {
            mTwitterSession = new MutableLiveData();
        }
        return mTwitterSession;
    }

    public MediatorLiveData<Integer> getStates() {
        if (states == null) {
            states = new MediatorLiveData();
        }
        return states;
    }
}
