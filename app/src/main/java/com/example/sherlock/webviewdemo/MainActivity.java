package com.example.sherlock.webviewdemo;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.HttpAuthHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity{

    //declare member variable
    private final static String CORESIGHT_URL = "http://hodwvpics01/Coresight/#/PBDisplays/45";
    private final static String TAG = "MainActivity";
    private WebView mWebview;
    private final Activity mActivity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate Called!");
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);

        mWebview = new WebView(this);
        setContentView(mWebview);

        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.getSettings().setLoadsImagesAutomatically(true);
        mWebview.clearCache(true);
        mWebview.clearFormData();
        mWebview.clearHistory();

        setUpWebClient();

        mWebview.loadUrl(CORESIGHT_URL);
    }

    private void setUpWebClient() {

        mWebview.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                mActivity.setProgress(progress * 1000);
            }
        });
        mWebview.setWebViewClient(new WebViewClient() {

            ProgressDialog pd = null;

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                pd = new ProgressDialog(MainActivity.this);
                pd.setTitle("Please wait");
                pd.setMessage("Loading Coresight..");
                pd.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pd.dismiss();
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(mActivity, "ERROR! " + description, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onReceivedHttpAuthRequest(final WebView view, final HttpAuthHandler handler,
                                                  final String host, final String realm) {

                pd.hide();
                final Dialog login = new Dialog(mActivity);
                login.setContentView(R.layout.login_dialog);

                Button btnLogin = (Button) login.findViewById(R.id.btnLogin);
                Button btnCancel = (Button) login.findViewById(R.id.btnCancel);

                final EditText txtUsername = (EditText) login.findViewById(R.id.txtUsername);
                final EditText txtPassword = (EditText) login.findViewById(R.id.txtPassword);

                // Attached listener for login GUI button
                btnLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String username = txtUsername.getText().toString().trim();
                        final String password = txtPassword.getText().toString().trim();
                        if (username.length() > 0 && password.length() > 0) {
                            // Validate Your login credential here than display message
                            view.setHttpAuthUsernamePassword(host, realm, username, password);
                            // Redirect to dashboard / home screen.
                            login.dismiss();

                            String[] up = view.getHttpAuthUsernamePassword(host, realm);
                            if (up != null && up.length == 2) {
                                handler.proceed(up[0], up[1]);
                                pd.show();
                            }
                        } else {
                            Toast.makeText(mActivity, "Please enter Username and Password", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.exit(0);
                    }
                });

                login.show();
            }
        });
    }
}
