package twiter.ginosi.com.twiterapi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.twitter.sdk.android.core.models.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import twiter.ginosi.com.twiterapi.helpers.ColorHelper;
import twiter.ginosi.com.twiterapi.helpers.ImagePickUpUtil;
import twiter.ginosi.com.twiterapi.helpers.PostImageView;
import twiter.ginosi.com.twiterapi.viewModel.HomeViewModel;
import twiter.ginosi.com.twiterapi.viewModel.UserPostViewModel;

public class CreatePostActivity extends AppActivity {
    public static final int REQUEST_INSERT = 4376;
    @BindView(R.id.post_text)
    EditText postText;

    @BindView(R.id.char_counter)
    TextView charCounter;

    @BindView(R.id.add_image)
    ImageButton addImage;

    @BindView(R.id.camera_photo)
    ImageButton cameraPhoto;

    @BindView(R.id.main_content)
    RelativeLayout rootView;

    @BindView(R.id.btn_close)
    ImageButton btnClose;

    @BindView(R.id.close_layout)
    LinearLayout closeLayout;

    @BindView(R.id.image_layout)
    RelativeLayout imageLayout;

    @BindView(R.id.upload_image)
    PostImageView uploadImage;

    @BindView(R.id.write_page_avatar)
    SimpleDraweeView writePageAvatar;

    @BindView(R.id.write_page_user_name)
    TextView userNameTextView;

    @BindView(R.id.write_page_post_btn)
    Button writePostBtn;

    private boolean hideSoftKeyboard;

    private Uri imageUri;
    private Uri selectedUri;
    private User item;
    private UserPostViewModel mModel;
    private ProgressDialog progressdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        setView();
        subscribeToModel();
        postBtnEnabled();
        openKeyboard();
    }


    private void setView(){
        setTitle(R.string.create_tweet);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        item = App.getInstance().getBus().popSticky(User.class);
        postText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                postBtnEnabled();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        addImage.getDrawable().mutate().setColorFilter(ColorHelper.getAttributeColor(addImage.getContext(), R.attr.textColorPrimaryColoredDark), PorterDuff.Mode.SRC_IN);
        cameraPhoto.getDrawable().mutate().setColorFilter(ColorHelper.getAttributeColor(cameraPhoto.getContext(), R.attr.textColorPrimaryColoredDark), PorterDuff.Mode.SRC_IN);

        RoundingParams roundingParams = new RoundingParams();
        roundingParams.setRoundAsCircle(true);
        writePageAvatar.getHierarchy().setRoundingParams(roundingParams);

        ImageRequest request =
                ImageRequestBuilder.newBuilderWithSource(Uri.parse(item.profileImageUrl))
                        .setProgressiveRenderingEnabled(false)
                        .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setAutoPlayAnimations(false)
                .build();


        writePageAvatar.setController(controller);

        userNameTextView.setText(item.name);
    }

    private void subscribeToModel() {
        mModel = ViewModelProviders.of(this).get(UserPostViewModel.class);
        mModel.getCreatedTweet().observe(this, tweet -> {

            App.getInstance().getBus().pushSticky(tweet);
            finish();
            setResult(REQUEST_INSERT, null);
        });

        mModel.getStates().observe(this, states -> {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        });
    }

    private void postBtnEnabled(){
        charCounter.setText(postText.length() + "/" + 256);

        boolean isEnabled =  postText.getText().toString().trim().length() > 0 || imageUri != null;;

        if(postText.getText().toString().trim().length() > 256){
            isEnabled = false;
        }
        writePostBtn.setEnabled(isEnabled);
        if(isEnabled)
            writePostBtn.setTextColor(ColorHelper.getAttributeColor(writePostBtn.getContext(), R.attr.textColorPrimaryColoredDark));
        else
            writePostBtn.setTextColor(getResources().getColor(R.color.gray));

    }

    @OnClick(R.id.btn_close)
    void deleteImageFromEditText() {
        uploadImage.setImageBitmap(null);
        imageLayout.setVisibility(View.GONE);
        imageButton(null);
        postBtnEnabled();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_SELECT_PICTURE) {
                selectedUri = data.getData();
                if (selectedUri != null){
                    uploadImage(selectedUri);
                }
            }else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                uploadImage(imageUri);
            }
        }
    }

    private void uploadImage(Uri imageUri){
        this.imageUri = imageUri;
        addImageInEditText(imageUri, imageUri);
        postBtnEnabled();
        hideSoftKeyboard = true;
    }

    private void addImageInEditText(Uri url, Object bitmap) {
        imageLayout.setVisibility(View.VISIBLE);
        uploadImage.setImageURI(url, closeLayout, btnClose);
        imageButton(bitmap);
    }

    private void imageButton(Object bitmap){
        addImage.setEnabled(bitmap == null);
        cameraPhoto.setEnabled(bitmap == null);
        if(bitmap == null){
            addImage.getDrawable().mutate().setColorFilter(ColorHelper.getAttributeColor(addImage.getContext(), R.attr.textColorPrimaryColoredDark), PorterDuff.Mode.SRC_IN);
            cameraPhoto.getDrawable().mutate().setColorFilter(ColorHelper.getAttributeColor(addImage.getContext(), R.attr.textColorPrimaryColoredDark), PorterDuff.Mode.SRC_IN);
        }else {
            cameraPhoto.getDrawable().mutate().setColorFilter(getResources().getColor(R.color.gray), PorterDuff.Mode.SRC_IN);
            addImage.getDrawable().mutate().setColorFilter(getResources().getColor(R.color.gray), PorterDuff.Mode.SRC_IN);
        }
    }

    @OnClick(R.id.add_image)
    void onAddImage(){
        promptExternalStoragePermissions((granted, showRatinale) -> {
            if (granted) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.choose_a_photo)), REQUEST_SELECT_PICTURE);
            } else {

                Snackbar snackbar = Snackbar.make(rootView, R.string.certificate_permission_rationale, Snackbar.LENGTH_LONG);

                if (!showRatinale) {
                    snackbar.setText(R.string.certificate_permission_denied);
                    snackbar.setAction(R.string.permission_open_settings, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    });
                }
                snackbar.show();
            }
        });

    }

    @OnClick(R.id.camera_photo)
    void imageFromCamera(){
        promptExternalStoragePermissions((granted, showRatinale) -> {
            if (granted) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "New Picture");
                    values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");

                    imageUri = getContentResolver().insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            } else {
                Snackbar snackbar = Snackbar.make(rootView, R.string.certificate_permission_rationale, Snackbar.LENGTH_LONG);

                if (!showRatinale) {
                    snackbar.setText(R.string.certificate_permission_denied);
                    snackbar.setAction(R.string.permission_open_settings, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    });
                }
                snackbar.show();
            }
        });
    }

    public void onRestart() {
        super.onRestart();
        openKeyboard();
    }

    private void openKeyboard(){
        if(hideSoftKeyboard){
            hideSoftKeyboard();
            hideSoftKeyboard = false;
        }else{
            new Handler().postDelayed(() -> showSoftKeyboard(postText), 200);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // API 5+ solution
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @OnClick(R.id.write_page_post_btn)
    void onWritePagePostBtn(){
        progressdialog = new ProgressDialog(this);
        progressdialog.setMessage("Please Wait....");
        progressdialog.show();
        if(imageUri== null){
            mModel.uploadTweet(postText.getText().toString(), null);
        }else{
            final String path = ImagePickUpUtil.getPath(this, imageUri);

            mModel.uploadTweet(postText.getText().toString(), path);

        }
        progressdialog.show();

    }

}
