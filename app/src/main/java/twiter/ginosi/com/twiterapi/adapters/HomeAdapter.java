package twiter.ginosi.com.twiterapi.adapters;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.twitter.sdk.android.core.models.Media;
import com.twitter.sdk.android.core.models.MediaEntity;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.TweetEntities;
import com.twitter.sdk.android.core.models.User;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.internal.Util;
import twiter.ginosi.com.twiterapi.App;
import twiter.ginosi.com.twiterapi.CreatePostActivity;
import twiter.ginosi.com.twiterapi.HomeActivity;
import twiter.ginosi.com.twiterapi.R;
import twiter.ginosi.com.twiterapi.helpers.DateTimeUtils;
import twiter.ginosi.com.twiterapi.helpers.ScaleControllerListener;
import twiter.ginosi.com.twiterapi.helpers.States;
import twiter.ginosi.com.twiterapi.helpers.Utils;

import static java.security.AccessController.getContext;

/**
 * Created by Davit Galstyan on 6/30/18.
 */
public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Object> items;
    private static final int ITEM_LOAD = -2;
    private static final int TYPE_PROFILE = -1;
    private int loadingMode = States.STATE_NONE;
    private Listener listener;

    public HomeAdapter() {
        items = new ArrayList<>();
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_LOAD) {
            return new LoadingViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_load_more, parent, false));
        }
        if (viewType == TYPE_PROFILE) {
            return new ProfileViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_profile, parent, false));
        }else{
            return new TwitsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_user_twits, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ProfileViewHolder) {
            ((ProfileViewHolder) holder).onBind((User)items.get(position));
            return;
        }

        if (holder instanceof TwitsViewHolder) {
            ((TwitsViewHolder) holder).onBind((Tweet)items.get(position));
            return;
        }

        if (holder instanceof LoadingViewHolder) {
            ((LoadingViewHolder) holder).bind();
            return;
        }

    }

    @Override
    public long getItemId(int position) {
        if (items.get(position) instanceof User)
            return ((User) items.get(position)).id;

        if (items.get(position) instanceof Tweet)
            return ((Tweet) items.get(position)).id;

        return -1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == items.size()) {
            // This is where we'll add footer.
            return ITEM_LOAD;
        }

        if (items.get(position) instanceof User) {
            return TYPE_PROFILE;
        }

        return 0;
    }

    public void setItems(List<Tweet> items) {
        int startIndex = this.items.size();
        this.items.addAll(startIndex, items);
        notifyItemRangeInserted(startIndex, items.size());
    }

    @Override
    public int getItemCount() {
        return items.size() + (loadingMode == 0 ? 0 : 1);
    }

    public void setProfile(User profile) {
        items.add(0, profile);
        notifyItemInserted(0);
    }

    public void addItem(Tweet tweet, int index) {
        items.add(index, tweet);
        notifyItemInserted(index);
    }

    class ProfileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.user_avatar)
        SimpleDraweeView userAvatar;

        @BindView(R.id.profile_name)
        TextView profileName;

        @BindView(R.id.twit_button)
        Button twitButton;

        private User user;

        public ProfileViewHolder(View itemView) {
            super(itemView);
            itemView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            ButterKnife.bind(this, itemView);

            RoundingParams roundingParams = new RoundingParams();
            roundingParams.setRoundAsCircle(true);
            userAvatar.getHierarchy().setRoundingParams(roundingParams);
            twitButton.setOnClickListener(this);

        }

        public void onBind(User user) {
            this.user= user;
            ImageRequest request =
                    ImageRequestBuilder.newBuilderWithSource(Uri.parse(user.profileImageUrl))
                            .setProgressiveRenderingEnabled(false)
                            .build();
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setAutoPlayAnimations(false)
                    .build();


            userAvatar.setController(controller);

            profileName.setText("@" + user.screenName);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.twit_button:
                    listener.onCreateTweets();
                    return;
            }
        }
    }

    class TwitsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.user_avatar)
        SimpleDraweeView userAvatar;

        @BindView(R.id.profile_name)
        TextView profileName;

        @BindView(R.id.twit_date)
        TextView dateView;

        @BindView(R.id.twit_image)
        SimpleDraweeView twitImage;

        @BindView(R.id.post_text)
        TextView postText;

        public TwitsViewHolder(View itemView) {
            super(itemView);
            itemView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            ButterKnife.bind(this, itemView);

            RoundingParams roundingParams = new RoundingParams();
            roundingParams.setRoundAsCircle(true);
            userAvatar.getHierarchy().setRoundingParams(roundingParams);

        }

        public void onBind(Tweet tweet) {
            twitImage.setVisibility(View.GONE);
            TweetEntities entities = tweet.entities;
            if (entities != null) {
                List<MediaEntity> mediaList = entities.media;
                for (MediaEntity media : mediaList) {
                    if (media.mediaUrl != null) {
                        twitImage.setVisibility(View.VISIBLE);
                           DraweeController controller = Fresco.newDraweeControllerBuilder()
                            .setUri(Uri.parse(media.mediaUrl))
                            .setOldController(twitImage.getController())
                            .setAutoPlayAnimations(true)
                            .setControllerListener(new ScaleControllerListener(twitImage, true, true))
                            .build();
                        twitImage.setController(controller);
                        break;
                    }
                }
            }
            ImageRequest request =
                    ImageRequestBuilder.newBuilderWithSource(Uri.parse(tweet.user.profileImageUrl))
                            .setProgressiveRenderingEnabled(false)
                            .build();
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setAutoPlayAnimations(false)
                    .build();


            userAvatar.setController(controller);

            profileName.setText(tweet.user.name);

            dateView.setText(DateTimeUtils.getDisplayDate(tweet.createdAt));
            postText.setText(tweet.text);
        }
    }

    public void setLoadingMode(int mode) {
        if (mode == loadingMode) return;

        int oldMode = loadingMode;
        loadingMode = mode;

        if (mode == 0) {
            notifyItemRemoved(items.size());
        } else if (oldMode == 0) {
            notifyItemInserted(items.size());
        } else {
            notifyItemChanged(items.size());
        }
    }

    public void reset(){
        items.clear();
        notifyDataSetChanged();
    }

    class LoadingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.load_text)
        TextView loadText;
        @BindView(R.id.load_button)
        Button loadButton;
        @BindView(R.id.load_circle)
        ProgressBar loadCircle;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            loadButton.setOnClickListener(this);
        }

        public void bind() {
            switch (loadingMode) {
                case States.STATE_NONE:
                    itemView.setVisibility(View.GONE);
                    return;
                case States.STATE_LOADING:
                    loadText.setVisibility(View.GONE);
                    loadButton.setVisibility(View.GONE);
                    loadCircle.setVisibility(View.VISIBLE);
                    break;
                case States.STATE_ERROR:
                    loadText.setVisibility(View.VISIBLE);
                    loadButton.setVisibility(View.VISIBLE);
                    loadButton.setText(R.string.feed_load_retry_button);
                    loadCircle.setVisibility(View.GONE);
                    break;
            }

            itemView.setVisibility(View.VISIBLE);

        }

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.load_button){
                listener.onLoadMoreClick();
            }
        }
    }

    public interface Listener {
        void onLoadMoreClick();
        void onCreateTweets();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

}
