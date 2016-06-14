package com.example.tnex.facebookpostingdemo.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Bundle;
import android.os.Handler;

import android.app.Activity;
import android.app.ProgressDialog;

import android.view.View;
import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tnex.facebookpostingdemo.R;
import com.example.tnex.facebookpostingdemo.executers.AsyncFacebookRunner;
import com.example.tnex.facebookpostingdemo.executers.BaseRequestListener;
import com.example.tnex.facebookpostingdemo.executers.Facebook;
import com.example.tnex.facebookpostingdemo.executers.SessionStore;

public class TestPostActivity extends Activity{
    private Facebook mFacebook;
    private CheckBox mFacebookCb;
    private ProgressDialog mProgress;

    private Handler mRunOnUi = new Handler();

    private static final String APP_ID = "app id here";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test_post);

        final EditText reviewEdit = (EditText) findViewById(R.id.revieew);
        mFacebookCb				  = (CheckBox) findViewById(R.id.cb_facebook);

        mProgress	= new ProgressDialog(this);

        mFacebook 	= new Facebook(APP_ID);

        SessionStore.restore(mFacebook, this);

        if (mFacebook.isSessionValid()) {
            mFacebookCb.setChecked(true);

            String name = SessionStore.getName(this);
            name		= (name.equals("")) ? "Unknown" : name;

            mFacebookCb.setText("  Facebook  (" + name + ")");
        }

        ((Button) findViewById(R.id.button1)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String review = reviewEdit.getText().toString();

                if (review.equals("")) return;

                if (mFacebookCb.isChecked()) postToFacebook(review);
            }
        });
    }

    private void postToFacebook(String review) {
        mProgress.setMessage("Posting ...");
        mProgress.show();

        AsyncFacebookRunner mAsyncFbRunner = new AsyncFacebookRunner(mFacebook);

        Bundle params = new Bundle();

        params.putString("message", review);
        params.putString("name", "Dexter");
        params.putString("caption", "londatiga.net");
        params.putString("link", "http://www.londatiga.net");
        params.putString("description", "Dexter, seven years old dachshund who loves to catch cats, eat carrot and krupuk");
        params.putString("picture", "http://twitpic.com/show/thumb/6hqd44");

        mAsyncFbRunner.request("me/feed", params, "POST", new WallPostListener());
    }

    private final class WallPostListener extends BaseRequestListener implements AsyncFacebookRunner.RequestListener {
        public void onComplete(final String response) {
            mRunOnUi.post(new Runnable() {
                @Override
                public void run() {
                    mProgress.cancel();

                    Toast.makeText(TestPostActivity.this, "Posted to Facebook", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}