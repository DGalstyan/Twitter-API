package twiter.ginosi.com.twiterapi.helpers;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import twiter.ginosi.com.twiterapi.R;

/**
 * Created by Davit Galstyan on 7/1/18.
 */
public class LoadingView extends LinearLayout {
    public static final int NONE = 0;
    public static final int LOADING = 1;
    public static final int ERROR = 2;


    private Runnable onRetryListener;
    private int mode = NONE;
    private int errorStringId = -1;
    private int loadingStringId = -1;

    private TextView textView;
    private View progressBar;
    private Button button;
    private View rootView ;


    public LoadingView(Context context) {
        super(context);
        init();
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    private void init(){
        rootView = inflate(getContext(), R.layout.view_loading, this);

        progressBar = rootView.findViewById(R.id.loading_view_progressbar);
        textView = rootView.findViewById(R.id.loading_view_message);
        button = rootView.findViewById(R.id.loading_view_action);

        button.setOnClickListener(view -> {
            if (onRetryListener != null) {
                onRetryListener.run();
            }
        });

        setMode(NONE);
    }

    public void setOnRetryListener(Runnable listener){
        onRetryListener = listener;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode){
        this.mode = mode;
        switch (mode){
            case NONE:
                setVisibility(GONE);
                break;
            case LOADING:
                if (loadingStringId != -1) {
                    textView.setText(loadingStringId);
                    textView.setVisibility(VISIBLE);
                } else {
                    textView.setText("");
                    textView.setVisibility(GONE);
                }
                progressBar.setVisibility(VISIBLE);
                button.setVisibility(GONE);
                setVisibility(VISIBLE);
                break;
            case ERROR:
                if (errorStringId != -1) {
                    textView.setText(errorStringId);
                    textView.setVisibility(VISIBLE);
                } else {
                    textView.setText("");
                    textView.setVisibility(GONE);
                }

                progressBar.setVisibility(GONE);
                button.setVisibility(VISIBLE);

                setVisibility(VISIBLE);
                break;
        }
    }

    public void setLoadingRes(@StringRes int id) {
        loadingStringId = id;
        if(mode == LOADING){
            if(loadingStringId != -1) {
                textView.setText(loadingStringId);
            }else {
                textView.setText("");
            }
        }
    }

    public void setErrorRes(@StringRes int id) {
        errorStringId = id;
        if(mode == ERROR && errorStringId != -1){
            textView.setText(errorStringId);
        }
    }

    public void setButtonRes(@StringRes int id) {
        if(id != -1){
            button.setText(id);
        }
    }
}