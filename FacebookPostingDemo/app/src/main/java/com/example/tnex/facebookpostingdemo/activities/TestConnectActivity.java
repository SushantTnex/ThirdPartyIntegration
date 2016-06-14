package com.example.tnex.facebookpostingdemo.activities;

        import android.os.Bundle;

        import com.example.tnex.facebookpostingdemo.R;
        import com.example.tnex.facebookpostingdemo.executers.DialogError;
        import com.example.tnex.facebookpostingdemo.executers.Facebook;
        import com.example.tnex.facebookpostingdemo.executers.FacebookError;
        import com.example.tnex.facebookpostingdemo.executers.SessionStore;

        import org.json.JSONObject;
        import org.json.JSONTokener;

        import android.app.Activity;
        import android.app.AlertDialog;
        import android.app.ProgressDialog;

        import android.content.DialogInterface;
        import android.content.Intent;
        import android.graphics.Color;

        import android.os.Bundle;
        import android.os.Handler;
        import android.os.Message;

        import android.view.View;
        import android.view.View.OnClickListener;

        import android.widget.Button;
        import android.widget.CheckBox;
        import android.widget.Toast;



public class TestConnectActivity extends Activity {
    private Facebook mFacebook;
    private CheckBox mFacebookBtn;
    private ProgressDialog mProgress;

    private static final String[] PERMISSIONS = new String[] {"publish_stream", "read_stream", "offline_access"};

    private static final String APP_ID = "1594285454234994";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test_connect);

        mFacebookBtn	= (CheckBox) findViewById(R.id.cb_facebook);

        mProgress		= new ProgressDialog(this);
        mFacebook		= new Facebook(APP_ID);

        SessionStore.restore(mFacebook, this);

        if (mFacebook.isSessionValid()) {
            mFacebookBtn.setChecked(true);

            String name = SessionStore.getName(this);
            name		= (name.equals("")) ? "Unknown" : name;

            mFacebookBtn.setText("  Facebook (" + name + ")");
            mFacebookBtn.setTextColor(Color.WHITE);
        }

        mFacebookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFacebookClick();
            }
        });

        ((Button) findViewById(R.id.button1)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TestConnectActivity.this, TestPostActivity.class));
            }
        });
    }

    private void onFacebookClick() {
        if (mFacebook.isSessionValid()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage("Delete current Facebook connection?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            fbLogout();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();

                            mFacebookBtn.setChecked(true);
                        }
                    });

            final AlertDialog alert = builder.create();

            alert.show();
        } else {
            mFacebookBtn.setChecked(false);

            mFacebook.authorize(this, PERMISSIONS, -1, new FbLoginDialogListener());
        }
    }

    private final class FbLoginDialogListener implements Facebook.DialogListener {
        public void onComplete(Bundle values) {
            SessionStore.save(mFacebook,getApplication());

            mFacebookBtn.setText("  Facebook (No Name)");
            mFacebookBtn.setChecked(true);
            mFacebookBtn.setTextColor(Color.WHITE);

            getFbName();
        }




        public void onFacebookError(FacebookError error) {
            Toast.makeText(TestConnectActivity.this, "Facebook connection failed", Toast.LENGTH_SHORT).show();

            mFacebookBtn.setChecked(false);
        }

        @Override
        public void onError(DialogError e) {

        }


        public void onCancel() {
            mFacebookBtn.setChecked(false);
        }
    }

    private void getFbName() {
        mProgress.setMessage("Finalizing ...");
        mProgress.show();

        new Thread() {
            @Override
            public void run() {
                String name = "";
                int what = 1;

                try {
                    String me = mFacebook.request("me");

                    JSONObject jsonObj = (JSONObject) new JSONTokener(me).nextValue();
                    name = jsonObj.getString("name");
                    what = 0;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                mFbHandler.sendMessage(mFbHandler.obtainMessage(what, name));
            }
        }.start();
    }

    private void fbLogout() {
        mProgress.setMessage("Disconnecting from Facebook");
        mProgress.show();

        new Thread() {
            @Override
            public void run() {
                SessionStore.clear(TestConnectActivity.this);

                int what = 1;

                try {
                    mFacebook.logout(TestConnectActivity.this);

                    what = 0;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                mHandler.sendMessage(mHandler.obtainMessage(what));
            }
        }.start();
    }

    private Handler mFbHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mProgress.dismiss();

            if (msg.what == 0) {
                String username = (String) msg.obj;
                username = (username.equals("")) ? "No Name" : username;

                SessionStore.saveName(username, TestConnectActivity.this);

                mFacebookBtn.setText("  Facebook (" + username + ")");

                Toast.makeText(TestConnectActivity.this, "Connected to Facebook as " + username, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(TestConnectActivity.this, "Connected to Facebook", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mProgress.dismiss();

            if (msg.what == 1) {
                Toast.makeText(TestConnectActivity.this, "Facebook logout failed", Toast.LENGTH_SHORT).show();
            } else {
                mFacebookBtn.setChecked(false);
                mFacebookBtn.setText("  Facebook (Not connected)");
                mFacebookBtn.setTextColor(Color.GRAY);

                Toast.makeText(TestConnectActivity.this, "Disconnected from Facebook", Toast.LENGTH_SHORT).show();
            }
        }
    };
}
