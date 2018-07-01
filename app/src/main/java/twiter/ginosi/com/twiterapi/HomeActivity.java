package twiter.ginosi.com.twiterapi;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.User;

import butterknife.BindView;
import butterknife.OnClick;
import twiter.ginosi.com.twiterapi.adapters.HomeAdapter;
import twiter.ginosi.com.twiterapi.helpers.LoadingView;
import twiter.ginosi.com.twiterapi.helpers.States;
import twiter.ginosi.com.twiterapi.viewModel.HomeViewModel;

import static twiter.ginosi.com.twiterapi.CreatePostActivity.REQUEST_INSERT;

public class HomeActivity extends AppActivity implements HomeAdapter.Listener{

    private ScrollListener scrollListener;
    private final int DEFAULT_THRESHOLD = 5;

    private HomeViewModel mModel;

    private HomeAdapter adapter;

    private LinearLayoutManager linearlayoutManager;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.loading_view)
    LoadingView loadingView;

    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setUpViews();
        subscribeToModel();
    }

    private void setUpViews() {
        adapter = new HomeAdapter();
        scrollListener = new ScrollListener();
        adapter.setListener(this);
        recyclerView.getItemAnimator().setChangeDuration(0);
        linearlayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearlayoutManager);

        recyclerView.setHasFixedSize(true);

        recyclerView.addOnScrollListener(scrollListener);
        recyclerView.setAdapter(adapter);

        refreshLayout.setOnRefreshListener(() -> mModel.refresh());

        refreshLayout.setOnRefreshListener(() -> {
                    if (!mModel.refresh()) {
                        refreshLayout.setRefreshing(false);
                    }
                }
        );

        loadingView.setErrorRes(R.string.no_internet_connection_message);
        loadingView.setLoadingRes(R.string.loading);
        loadingView.setOnRetryListener(() -> {
            if(user == null) {
                mModel.getUserInfo();
            }
            mModel.reload();
        });
    }


    private void subscribeToModel() {
        // Get the ViewModel.
        mModel = ViewModelProviders.of(this).get(HomeViewModel.class);

        // Create the observer which updates the UI.
        mModel.getCurrentUser().observe(this, user -> {
            this.user = user;
            adapter.setProfile(user);
        });

        mModel.getState().observe(this, state -> {
            if (state != States.STATE_REFRESHING) {
                refreshLayout.setRefreshing(false);
            }
            if (state == States.STATE_RESET_ALL) {
                adapter.reset();
                adapter.setProfile(user);
            }
            if (mModel.hasData()) {
                loadingView.setMode(LoadingView.NONE);
                switch (state) {
                    case States.STATE_LOADING:
                        if (adapter.getItemCount() >= 1) {
                            adapter.setLoadingMode(States.STATE_LOADING);
                        } else {
                            loadingView.setMode(LoadingView.LOADING);
                        }
                        break;
                    case States.STATE_ERROR:
                        adapter.setLoadingMode(States.STATE_ERROR);
                        break;
                    default:
                        adapter.setLoadingMode(States.STATE_NONE);
                        break;
                }
            }else{
                adapter.setLoadingMode(States.STATE_NONE);
                switch (state) {
                    case States.STATE_LOADING:
                        loadingView.setMode(LoadingView.LOADING);
                        adapter.reset();
                        break;
                    case States.STATE_ERROR:
                        loadingView.setMode(LoadingView.ERROR);
                        adapter.reset();
                        break;
                    case States.STATE_REACHED_END:
                        loadingView.setMode(LoadingView.NONE);
                    default:
                        loadingView.setMode(LoadingView.NONE);
                        break;
                }
            }
        });

        mModel.getTweets().observe(this, twits -> {
            adapter.setItems(twits);
        });

        mModel.initialize();
    }

    @Override
    public void onLoadMoreClick() {
        mModel.loadMore();
    }

    @Override
    @OnClick(R.id.create_post)
    public void onCreateTweets() {
        if(user == null) return;
        Intent intent = new Intent(this, CreatePostActivity.class);
        App.getInstance().getBus().pushSticky(user);
        startActivityForResult(intent, REQUEST_INSERT);
    }

    private class ScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if(dy <= 0) return;

            checkLoad(true);
        }
    }


    @Override
    public void onRestart() {
        super.onRestart();
        if(linearlayoutManager != null && linearlayoutManager.getItemCount() == 0) {
            checkLoad(false);
        }
    }

    protected void checkLoad(boolean isScroll) {
        int totalItemCount = linearlayoutManager.getItemCount();
        int lastVisibleItem = linearlayoutManager.findLastVisibleItemPosition();

        if (totalItemCount - DEFAULT_THRESHOLD <= lastVisibleItem) {
            if(isScroll) recyclerView.post(()->loadMore()); else loadMore();
        }
    }

    protected void loadMore(){
        mModel.loadMore();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        recyclerView.removeOnScrollListener(scrollListener);
        recyclerView = null;
        linearlayoutManager = null;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_INSERT) {
            Tweet item = App.getInstance().getBus().popSticky(Tweet.class);
            if (item != null) {
                mModel.refresh();
            }
        }
    }

}
