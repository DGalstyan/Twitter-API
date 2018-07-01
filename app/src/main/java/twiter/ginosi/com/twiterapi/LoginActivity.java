package twiter.ginosi.com.twiterapi;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import twiter.ginosi.com.twiterapi.viewModel.GeneralViewModel;

public class LoginActivity extends AppCompatActivity {
    private GeneralViewModel mModel;

    @BindView(R.id.twitter_button)
    TwitterLoginButton twitterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        //check for saved log in details..
        checkForSavedLogin();

        subscribeToModel();
        twitterLogin();

    }

    private void checkForSavedLogin() {
        TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
        if (session == null) return;	//if there are no credentials stored then return to usual activity
        App.getInstance().initClient(session);

        startFirstActivity();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        twitterButton.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * twitterLogin() method is responsible for authorize twitterAuthClient instance, since it has too callback one for
     * success and the other for failer
     */
    private void twitterLogin() {
        mModel.setUpTwitterButton(twitterButton);
    }

    private void subscribeToModel() {
        // Get the ViewModel.
        mModel = ViewModelProviders.of(this).get(GeneralViewModel.class);
        mModel.getTwitterSession().observe(this, twitterSession -> {
            App.getInstance().initClient(twitterSession);
            startFirstActivity();
        });

        mModel.getStates().observe(this, states -> {
            Toast.makeText(this, "Error with login", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * HomeActivity activity to display
     */
    private void startFirstActivity() {
        Intent intent = new Intent(getBaseContext(), HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
