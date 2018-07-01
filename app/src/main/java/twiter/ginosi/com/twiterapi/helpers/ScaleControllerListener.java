package twiter.ginosi.com.twiterapi.helpers;

import android.graphics.drawable.Animatable;
import android.support.annotation.Nullable;

import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;

import twiter.ginosi.com.twiterapi.R;

/**
 * Created by Davit Galstyan on 6/30/18.
 */
public class ScaleControllerListener extends BaseControllerListener<ImageInfo> {
    private SimpleDraweeView image;
    private boolean isScaleWidth;
    private boolean fullHeight;

    public ScaleControllerListener(SimpleDraweeView image, boolean isScaleWidth, boolean fullHeight){
        this(image, isScaleWidth);
        this.fullHeight = fullHeight;
    }

    public ScaleControllerListener(SimpleDraweeView image, boolean isScaleWidth){
        this.image = image;
        this.isScaleWidth = isScaleWidth;
    }

    @Override
    public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {
        if(fullHeight){
            updateFullScreenSize(imageInfo);
        }else{
            updateViewSize(imageInfo);
        }
    }

    @Override
    public void onFinalImageSet(String id, @Nullable final ImageInfo imageInfo, @Nullable Animatable animatable) {
        if(fullHeight){
            updateFullScreenSize(imageInfo);
        }else{
            updateViewSize(imageInfo);
        }
    }


    void updateViewSize(@Nullable ImageInfo imageInfo) {
        if (imageInfo != null) {
            int screenWidth = image.getResources().getDisplayMetrics().widthPixels;

            float maxHeight = image.getResources().getDimension(R.dimen.image_max_height);
            float maxWidth = image.getResources().getDimension(R.dimen.image_max_width);


            float aspect = 1f * imageInfo.getWidth() / imageInfo.getHeight();
            float height = screenWidth / aspect;


            if (height > maxHeight) {
                aspect = screenWidth / maxHeight;
            }
            if (screenWidth > maxWidth && isScaleWidth) {
                aspect = screenWidth / (maxWidth / aspect);
            }
            image.setAspectRatio(aspect);
            image.requestLayout();
        }
    }

    void updateFullScreenSize(@Nullable ImageInfo imageInfo) {
        if (imageInfo != null) {
            int screenWidth = image.getResources().getDisplayMetrics().widthPixels;
            float maxWidth = image.getResources().getDimension(R.dimen.image_max_width);
            float aspect = 1f * imageInfo.getWidth() / imageInfo.getHeight();

            if (screenWidth > maxWidth && isScaleWidth) {
                aspect = screenWidth / (maxWidth / aspect);
            }

            image.setAspectRatio(aspect);
            image.requestLayout();
        }
    }
}
