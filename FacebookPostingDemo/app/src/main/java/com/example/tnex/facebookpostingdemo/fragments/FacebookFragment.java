package com.example.tnex.facebookpostingdemo.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tnex.facebookpostingdemo.activities.TestPostActivity;
import com.example.tnex.facebookpostingdemo.utils.Utils;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.example.tnex.facebookpostingdemo.R;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.LikeView;
import com.facebook.share.widget.MessageDialog;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;

import java.io.File;


public class FacebookFragment extends Fragment implements View.OnClickListener {
    private static final String TAG  = "FacebookFragment";

    private LoginButton mButtonLogin;
    private TextView tvFirstName,tvLastName,tvUserId;
    private ImageView ivProfilePic;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private boolean isLoggedIn=false;
    // share button
    private ShareButton shareButton;
    //counter
    private int counter = 0;
    private Button btnSendPost;
    private LikeView likeView;
    CallbackManager callbackManager;
    ShareDialog shareDialog;


    // Callback
    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken accessToken = loginResult.getAccessToken();
            isLoggedIn=true;
            Profile profile = Profile.getCurrentProfile();
            displayMessage(profile);
        }

        @Override
        public void onCancel() {}

        @Override
        public void onError(FacebookException e) {}
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the Facebook SDk
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());

        callbackManager = CallbackManager.Factory.create();

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken
                    currentAccessToken) {
            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {

            }
        };

        accessTokenTracker.startTracking();
        profileTracker.startTracking();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_facebook, container, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initUI(view);

    }

    private void initUI(View view) {

        tvFirstName = (TextView) view.findViewById(R.id.tv_first_name);
        tvLastName = (TextView) view.findViewById(R.id.tv_last_name);
        tvUserId = (TextView) view.findViewById(R.id.tv_user_id);
        ivProfilePic = (ImageView) view.findViewById(R.id.iv_profile_pic);
        btnSendPost = (Button) view.findViewById(R.id.btn_sendPost);
        mButtonLogin = (LoginButton) view.findViewById(R.id.login_button);
        likeView = (LikeView) view.findViewById(R.id.like_view);

        mButtonLogin.setReadPermissions("user_friends");
        btnSendPost.setOnClickListener(this);
        mButtonLogin.setFragment(this);
        mButtonLogin.registerCallback(callbackManager, callback);
        shareButton = (ShareButton) view.findViewById(R.id.share_btn);
        shareButton.setOnClickListener(this);

        // For giving like to the page providede by url
        likeView.setObjectIdAndType("https://www.facebook.com/FacebookDevelopers",
                LikeView.ObjectType.PAGE);

        //for sharing something on wall
        sharePostOnWall();
       // shareMediaOnWall();



    }

  /*  private void shareMediaOnWall() {
        Uri photo1= Uri.parse("http://science-all.com/images/wallpapers/technology-images/technology-images-5.png");
        Uri photo2= Uri.parse("http://science-all.com/images/wallpapers/technology-images/technology-images-2.jpg");
        Uri photo3= Uri.parse("http://www.linkedstrategies.com/wp-content/uploads/2010/10/technology-the-basic-right-of-all-people-5.jpg");


        SharePhoto sharePhoto1 = new SharePhoto.Builder()
                .setBitmap()
        .build();
        SharePhoto sharePhoto2 = new SharePhoto.Builder()
                .setBitmap(...)
        .build();
        ShareVideo shareVideo1 = new ShareVideo.Builder()
                .setLocalUrl(...)
        .build();
        ShareVideo shareVideo2 = new ShareVideo.Builder()
                .setLocalUrl(...)
        .build();

        ShareContent shareContent = new ShareMediaContent.Builder()
                .addMedium(sharePhoto1)
                .addMedium(sharePhoto2)
                .addMedium(shareVideo1)
                .addMedium(shareVideo2)
                .build();

        ShareDialog shareDialog = new ShareDialog(...);
        shareDialog.show(shareContent, ShareDialog.Mode.AUTOMATIC);
    }*/
    private void sharePostOnWall() {
        String fileName = "YourImage.png";
        String completePath = Environment.getExternalStorageDirectory() + "/" + fileName;

        File file = new File(completePath);
        Uri imageUri = Uri.fromFile(file);
        Log.d(TAG, "sharePostOnWall: "+imageUri);
        FacebookSdk.sdkInitialize(getContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        // this part is optional
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }


        });
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle("Hello Facebook")
                    .setContentDescription(
                            "The 'Hello Facebook' sample  showcases simple Facebook integration")

                    .setContentUrl(Uri.parse("http://blogs-images.forbes.com/joshbersin/files/2014/10/t.jpg"))
                    .setImageUrl(Uri.parse("http://blogs-images.forbes.com/tykiisel/files/2013/03/14594978_WelcomeToTheFuture_03112013Final1.jpg"))
                    .build();
            shareDialog.show(linkContent);

        }

    }




    private void displayMessage(Profile profile){
        Utils.showVerboesLog(TAG, "Inside displayMessage()");
        if(profile != null){
            Utils.showVerboesLog(TAG, "Profile is not null=");
            tvFirstName.setText(profile.getFirstName());
            tvLastName.setText(profile.getLastName());
            tvUserId.setText(profile.getId());
            profile.getProfilePictureUri(100,100);
           // ivProfilePic.setImageBitmap(profile.getProfilePictureUri(100,100));
            Uri profilePicUri =  profile.getProfilePictureUri(100, 100);
            Utils.showVerboesLog(TAG, "first Name=" + profile.getFirstName());
            Utils.showVerboesLog(TAG,"Last Name="+profile.getLastName());
            Utils.showVerboesLog(TAG,"User Id="+profile.getLastName());
            Utils.showVerboesLog(TAG, "Profile Pic=" + profilePicUri);
        }
        else{
            Utils.showVerboesLog(TAG, "Profile is  null=");
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        Profile profile = Profile.getCurrentProfile();
        displayMessage(profile);
    }

    @Override
    public void onStop() {
        super.onStop();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_sendPost:
                Intent intentGoAndTest = new Intent(getActivity(),TestPostActivity.class);
                startActivity(intentGoAndTest);
                break;

            case R.id.share_btn:
                Utils.showVerboesLog(TAG,"Share button clicked");

                int result = postPicture();
                Utils.showVerboesLog(TAG,"result=="+result);

                if(counter==1){
                    Utils.showVerboesLog(TAG,"Successfully posted on wall");
                }
                break;

        }

    }

    // Method for posting image on wall
    public int  postPicture() {
        Utils.showVerboesLog(TAG, "User is in postPicture() ");
        Bitmap image =  BitmapFactory.decodeResource(this.getResources(), R.drawable.backbtn);
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(image)
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();
        if(photo!=null && content!=null) {
            Utils.showVerboesLog(TAG, "content not null ");
            counter = 1;
        }
        MessageDialog.show(this, content);
        return counter;
        }

    }



