package twiter.ginosi.com.twiterapi;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import twiter.ginosi.com.twiterapi.helpers.Utils;
import twiter.ginosi.com.twiterapi.network.InternetReceiver;
import twiter.ginosi.com.twiterapi.network.NetworkReceiverListener;

/**
 * Created by Davit Galstyan on 6/30/18.
 */
public class AppActivity extends AppCompatActivity implements NetworkReceiverListener {
    private InternetReceiver networkReceiver;
    private final String CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

    private SparseArray<PermissionRequestCallback> permissionRequestCallbacks = new SparseArray<>();
    protected static final int REQUEST_SELECT_PICTURE = 24531;
    protected static final int WRITE_STORAGE_PERMISSION_REQUEST_CODE = 312;
    protected static final int REQUEST_IMAGE_CAPTURE = 1;

    @BindView(R.id.toolbar_sub_error_bar)
    View errorBar;

    @BindView(R.id.toolbarErrorText)
    TextView toolbarErrorText;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
        networkReceiver = new InternetReceiver();
        networkReceiver.addListener(this);
        this.registerReceiver(networkReceiver, new IntentFilter(CONNECTIVITY_CHANGE_ACTION));
    }


    public boolean promptExternalStoragePermissions(PermissionRequestCallback callback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionRequestCallbacks.put(WRITE_STORAGE_PERMISSION_REQUEST_CODE, callback);
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_STORAGE_PERMISSION_REQUEST_CODE);
                return false;
            }
        }
        if (callback != null) {
            callback.onResponse(true, false);
        }
        return true;
    }

    public interface PermissionRequestCallback {
        void onResponse(boolean granted, boolean showRationale);
    }

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public boolean hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            return  true;
        }else {
            inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getApplicationWindowToken(), 0);
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        if (networkReceiver != null) {
            this.unregisterReceiver(networkReceiver);
        }
        super.onDestroy();
    }

    private void hideErrorsBar(boolean hide) {
        try {
            if (errorBar != null) {
                //TODO some animation here?
                if (hide && Utils.isOnline(this)) {
                    new CountDownTimer(5000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            errorBar.setBackgroundColor(ContextCompat.getColor(AppActivity.this, R.color.green));
                            toolbarErrorText.setText(getResources().getString(R.string.connected));
                        }

                        @Override
                        public void onFinish() {
                            // do something end times 5s
                            toolbarErrorText.setText(getResources().getString(R.string.waiting_for_network));
                            errorBar.setBackgroundColor(ContextCompat.getColor(AppActivity.this, R.color.network));
                            errorBar.setVisibility(View.GONE);
                        }
                    }.start();

                } else errorBar.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void networkAvailable() {
        hideErrorsBar(true);
    }

    @Override
    public void networkUnavailable() {
        hideErrorsBar(false);
    }
}
