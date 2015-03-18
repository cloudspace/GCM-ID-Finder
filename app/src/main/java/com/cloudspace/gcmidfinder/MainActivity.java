package com.cloudspace.gcmidfinder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;


public class MainActivity extends Activity {
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * Tag used on log messages.
     */
    static final String TAG = "GCMDemo";

    TextView mDisplay;
    GoogleCloudMessaging gcm;
    Context context;
    EditText input;
    Button requestButton;
    ViewSwitcher progressSwitcher;

    private View.OnClickListener requestClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            removeShareButton();
            InputMethodManager imm = (InputMethodManager)getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            progressSwitcher.setVisibility(View.VISIBLE);
            progressSwitcher.setDisplayedChild(1);
            // Check device for Play Services APK. If check succeeds, proceed with
            //  GCM registration.
            if (checkPlayServices()) {
                gcm = GoogleCloudMessaging.getInstance(MainActivity.this);
                registerInBackground();
            } else {
                mDisplay.setText("Error: No valid Google Play Services APK found.");
                progressSwitcher.setDisplayedChild(0);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        progressSwitcher = (ViewSwitcher) findViewById(R.id.switchy);
        mDisplay = (TextView) findViewById(R.id.output);
        input = (EditText) findViewById(R.id.input);
        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_GO) {
                    requestClickListener.onClick(textView);
                }
                return false;
            }
        });
        context = getApplicationContext();
        requestButton = (Button) findViewById(R.id.button);
        requestButton.setOnClickListener(requestClickListener);
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                mDisplay.setText("Error: This device is not supported.");
            }
            return false;
        }
        return true;
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, String, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg;
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    msg = gcm.register(input.getText().toString());
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                mDisplay.setText(msg);
                progressSwitcher.setDisplayedChild(0);
                addShareButton();
            }
        }.execute(null, null, null);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, mDisplay.getText().toString());
        startActivity(Intent.createChooser(sharingIntent, "Share to"));
        return super.onMenuItemSelected(featureId, item);
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        String regId = mDisplay.getText().toString();

        if (regId.isEmpty()|| regId.contains("Error")) {
            menu.clear();
        } else {
            if (!menu.hasVisibleItems()) {
                menu.add(0, 0, 0, "Share").setIcon(R.drawable.ic_share_white_48dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void addShareButton() {
        invalidateOptionsMenu();
    }

    private void removeShareButton() {
        invalidateOptionsMenu();
    }

}
