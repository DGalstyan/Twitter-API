package twiter.ginosi.com.twiterapi.viewModel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.tweetui.TimelineResult;
import com.twitter.sdk.android.tweetui.UserTimeline;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import twiter.ginosi.com.twiterapi.App;
import twiter.ginosi.com.twiterapi.helpers.States;

/**
 * Created by Davit Galstyan on 6/30/18.
 */
public class HomeViewModel extends ViewModel  {
    private boolean isLoading;
    private Long index;
    protected boolean isInitialized;
    protected boolean hasReachedEnd;

    // Create a LiveData with a User
    private MutableLiveData<User> mCurrentUser;

    private MutableLiveData<List<Tweet>> mTwits;

    private MutableLiveData<Integer> state= new MutableLiveData();

    public MutableLiveData<Integer> getState() {
        if (state == null) {
            state = new MutableLiveData();
        }
        return state;
    }

    public MutableLiveData<User> getCurrentUser() {
        if (mCurrentUser == null) {
            mCurrentUser = new MutableLiveData();
        }
        return mCurrentUser;
    }

    public MutableLiveData<List<Tweet>> getTweets() {
        if (mTwits == null) {
            mTwits = new MutableLiveData();
        }
        return mTwits;
    }

    private void getUserInfo(){
        Call<User> call = App.getInstance().getNetworkHttpClient().getCustomService().show(App.getInstance().getNetworkHttpClient().getUserId());
        call.enqueue(new retrofit2.Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.v("TTTTT", ""+call);
                mCurrentUser.postValue(response.body());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.v("TTTTT", ""+call);
            }
        });
    }

    private void getAllUserTweet(boolean refresh){
        isLoading = true;
        Long lastId = refresh ? null : index;


        UserTimeline userTimeline = new UserTimeline.Builder()
                .screenName(App.getInstance().getNetworkHttpClient().getUserName())
                .build();

        userTimeline.previous(lastId, new Callback<TimelineResult<Tweet>>() {
            @Override
            public void success(Result<TimelineResult<Tweet>> result) {
                state.postValue(States.STATE_REACHED_END);
                index = result.data.items.get(result.data.items.size() - 1).id;
                if(result.data.items.size() < 30){
                    hasReachedEnd = true;
                }


                if(refresh){
                    state.postValue(States.STATE_RESET_ALL);
                }else if(hasReachedEnd) {
                    state.postValue(States.STATE_REACHED_END);
                }else{
                    state.postValue(States.STATE_NONE);
                }

                mTwits.postValue(result.data.items);
                isLoading = false;
            }

            @Override
            public void failure(TwitterException exception) {
                isLoading = false;
                state.postValue(States.STATE_ERROR);
            }
        });
    }

    public boolean refresh() {
        if (isLoading) return false;
        state.postValue(States.STATE_REFRESHING);
        resetAll();
        getAllUserTweet(true);
        return true;
    }

    private void resetAll() {
        mTwits.postValue(new ArrayList<>());
        hasReachedEnd = false;
        index = null;
    }

    public void reload() {
        state.postValue(States.STATE_LOADING);
        resetAll();
        getAllUserTweet(false);
    }

    public void initialize() {
        if(isInitialized) return;
        getUserInfo();
        reload();
        isInitialized = true;
    }

    public void loadMore() {
        if(isLoading || hasReachedEnd) return;
        state.postValue(States.STATE_LOADING);
        getAllUserTweet(false);
    }

    public boolean hasData(){
        return index != null;
    }

}
