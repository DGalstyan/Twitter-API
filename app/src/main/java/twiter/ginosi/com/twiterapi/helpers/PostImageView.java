package twiter.ginosi.com.twiterapi.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.Priority;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import twiter.ginosi.com.twiterapi.R;

/**
 * Created by Davit Galstyan on 6/30/18.
 */
public class PostImageView extends SimpleDraweeView {


    public PostImageView(Context context) {
        super(context);
    }

    public PostImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PostImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }


    public void setImageURI(@Nullable String uriString, LinearLayout infoLayout, ImageButton btnInfo) {
        scaleImage(btnInfo, infoLayout, uriString);
    }

    public void scaleImage(ImageButton btnInfo, LinearLayout infoLayout, String uriString){
        ImageRequest imageRequest = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse(uriString))
                .setRequestPriority(Priority.HIGH)
                .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(imageRequest)
                .setOldController(getController())
                .setTapToRetryEnabled(true)
                .setControllerListener(new ScaleControllerListener(this, false, true))
                .build();


        processImageWithPaletteApi(btnInfo, infoLayout, imageRequest, controller);
        //setController(controller);
    }

    private void processImageWithPaletteApi(ImageButton btnInfo, LinearLayout infoLayout, ImageRequest request, DraweeController controller) {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>> dataSource =
                imagePipeline.fetchDecodedImage(request, getContext());
        dataSource.subscribe(new BaseBitmapDataSubscriber() {
            @Override
            protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {

            }

            @Override
            protected void onNewResultImpl(@Nullable Bitmap bitmap) {
                if(bitmap==null){
                    return;
                }

                int size = (int) Math.min(Math.min(Math.max(bitmap.getWidth(), bitmap.getHeight()) * 0.1, bitmap.getWidth()), bitmap.getHeight());
                Palette.from(bitmap).setRegion(bitmap.getWidth() - size, 0, bitmap.getWidth(), size).generate(new Palette.PaletteAsyncListener() {
                    public void onGenerated(Palette palette) {
                        // Use generated instance
                        int color = palette.getDominantColor(getResources().getColor(R.color.info_background));

                        infoLayout.setBackgroundColor(color);

                        btnInfo.getDrawable().setColorFilter(ColorHelper.getContrastColor(color), PorterDuff.Mode.SRC_IN);
                    }
                });
            }
        }, CallerThreadExecutor.getInstance());

        setController(controller);
    }

    public void setImageURI(@Nullable Uri uri, LinearLayout infoLayout, ImageButton btnInfo) {

        ImageRequest imageRequest =
                ImageRequestBuilder.newBuilderWithSource(uri)
                        .setRequestPriority(Priority.HIGH)
                        .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
                        .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(imageRequest)
                .setOldController(getController())
                .setTapToRetryEnabled(true)
                .setControllerListener(new ScaleControllerListener(this, false, true))
                .build();
        setController(controller);

        processImageWithPaletteApi(btnInfo, infoLayout, imageRequest, controller);
    }
}