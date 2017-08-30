package com.demo.thirdeye;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.thirdeye.beans.UserProfile;
import com.demo.thirdeye.utility.MongoDBConnector;
import com.demo.thirdeye.utility.PhoneDBConnector;
import com.demo.thirdeye.utility.Settings;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginMainActivity extends AppCompatActivity  {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;



    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView phoneOrUserIDView;
    private EditText mPasswordView;
    private View mProgressView;
    private TextView loginHeading;
    private TextView forgetPassword;
    private Button mEmailSignInButton;
    private CheckBox keepMeSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_main);
        // Set up the login form.
        keepMeSignIn = (CheckBox)findViewById(R.id.keepMeLoginFromLogin);
        loginHeading = (TextView)findViewById(R.id.loginHeading);
        forgetPassword = (TextView) findViewById(R.id.forgetPassword);
        phoneOrUserIDView = (AutoCompleteTextView) findViewById(R.id.loginEmail);
        TextInputLayout emailLayout = (TextInputLayout)findViewById(R.id.emailLayout);
        TextInputLayout passwordLayout = (TextInputLayout)findViewById(R.id.passwordLayout);
        mPasswordView = (EditText) findViewById(R.id.loginPassword);
        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mProgressView = findViewById(R.id.login_progress);

        loginHeading.setTypeface(Settings.AGENCY_FB);
        forgetPassword.setTypeface(Settings.AGENCY_FB);
        phoneOrUserIDView.setTypeface(Settings.AGENCY_FB);
        emailLayout.setTypeface(Settings.AGENCY_FB);
        passwordLayout.setTypeface(Settings.AGENCY_FB);
        mPasswordView.setTypeface(Settings.AGENCY_FB);
        mEmailSignInButton.setTypeface(Settings.AGENCY_FB);
        keepMeSignIn.setTypeface(Settings.AGENCY_FB);

        //populateAutoComplete();
        forgetPassword.setPaintFlags(forgetPassword.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        forgetPassword.setText("Forget Password?");

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        ImageView skip = (ImageView) findViewById(R.id.skipFromLoginPage);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent homPage = new Intent(LoginMainActivity.this,HomePage.class);
                LoginMainActivity.this.startActivity(homPage);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent homPage = new Intent(LoginMainActivity.this,HomePage.class);
        LoginMainActivity.this.startActivity(homPage);
        finish();
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        phoneOrUserIDView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String phoneOrUserID = phoneOrUserIDView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid phone number or user ID.
        if (TextUtils.isEmpty(phoneOrUserID)) {
            phoneOrUserIDView.setError(getString(R.string.error_field_required));
            focusView = phoneOrUserIDView;
            cancel = true;
        } else if (!isPhoneNumberValid(phoneOrUserID) && !userIdValid(phoneOrUserID)) {
            phoneOrUserIDView.setError(getString(R.string.error_invalid_Phone_number_or_userID));
            focusView = phoneOrUserIDView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            mAuthTask = new UserLoginTask(phoneOrUserID, password,keepMeSignIn.isChecked());
            mAuthTask.execute((Void) null);
        }
    }
    private boolean userIdValid(String userID) {
        //TODO: Replace this with your own logic
        String userIDPattern = "[a-zA-Z0-9_]+";
        return userID.matches(userIDPattern);
    }
    private boolean isPhoneNumberValid(String phone) {
        //TODO: Replace this with your own logic
        String mobilePattern = "[0-9]{10}";
        return phone.matches(mobilePattern);
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 8;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mEmailSignInButton.setVisibility(show ? View.GONE : View.VISIBLE);
            mEmailSignInButton.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mEmailSignInButton.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mEmailSignInButton.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }



    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Integer> {

        private final String phoneNoOrEmail;
        private final String mPassword;
        private final boolean keepMeSignIn;

        UserLoginTask(String phoneNoOrEmail, String password,boolean keepMeSignIn) {
            this.phoneNoOrEmail = phoneNoOrEmail;
            mPassword = password;
            this.keepMeSignIn = keepMeSignIn;
        }

        @Override
        protected Integer doInBackground(Void... params) {

            MongoDBConnector mongoDBConnector = new MongoDBConnector(LoginMainActivity.this);
            if (!Settings.INTERNET_STATUS) {
                return 1;
            }
            mongoDBConnector.Login(phoneNoOrEmail,mPassword,keepMeSignIn);
            return 0;
        }

        @Override
        protected void onPostExecute(final Integer status) {
            mAuthTask = null;

        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

}

