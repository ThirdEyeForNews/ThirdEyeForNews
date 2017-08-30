package com.demo.thirdeye;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.thirdeye.beans.UserProfile;
import com.demo.thirdeye.utility.MongoDBConnector;
import com.demo.thirdeye.utility.Settings;

public class SignUpPage extends AppCompatActivity {

    EditText userName,mobileNo,emailID,password,confirmPassword;
    CheckBox keepMeSignIn, termsAndCondition;
    Button signUp;
    private View mProgressView;
    UserSignUpTask signUpTask = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

        TextInputLayout userNameLayout,mobileNoLayout,emailIdLayout,passwordLayout,confirmPasswordLayout;
        TextView signUpHeading1,signUpHeading2;
        signUpHeading1 = (TextView) findViewById(R.id.signUp_title);
        signUpHeading2 = (TextView) findViewById(R.id.signupHeading);

        userNameLayout = (TextInputLayout) findViewById(R.id.usernameLayout);
        mobileNoLayout = (TextInputLayout) findViewById(R.id.mobileNumberLayout);
        emailIdLayout = (TextInputLayout) findViewById(R.id.emailLayout);
        passwordLayout = (TextInputLayout) findViewById(R.id.passwordLayout);
        confirmPasswordLayout = (TextInputLayout) findViewById(R.id.confirmPasswordLayout);

        userName = (EditText) findViewById(R.id.username);
        mobileNo = (EditText) findViewById(R.id.mobileNumber);
        emailID = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        confirmPassword = (EditText) findViewById(R.id.confirmPassword);
        keepMeSignIn = (CheckBox) findViewById(R.id.keepMeSignIn);
        termsAndCondition = (CheckBox) findViewById(R.id.termsAndConditions);
        mProgressView = findViewById(R.id.signup_progress);

        signUp = (Button) findViewById(R.id.email_sign_up_button);

        signUpHeading1.setTypeface(Settings.AGENCY_FB);
        signUpHeading2.setTypeface(Settings.AGENCY_FB);
        userName.setTypeface(Settings.AGENCY_FB);
        mobileNo.setTypeface(Settings.AGENCY_FB);
        emailID.setTypeface(Settings.AGENCY_FB);
        password.setTypeface(Settings.AGENCY_FB);
        confirmPassword.setTypeface(Settings.AGENCY_FB);
        userNameLayout.setTypeface(Settings.AGENCY_FB);
        mobileNoLayout.setTypeface(Settings.AGENCY_FB);
        emailIdLayout.setTypeface(Settings.AGENCY_FB);
        passwordLayout.setTypeface(Settings.AGENCY_FB);
        confirmPasswordLayout.setTypeface(Settings.AGENCY_FB);
        signUp.setTypeface(Settings.AGENCY_FB);
        keepMeSignIn.setTypeface(Settings.AGENCY_FB);
        termsAndCondition.setTypeface(Settings.AGENCY_FB);


        ImageView skip = (ImageView) findViewById(R.id.skipFromSignUpPage);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent homPage = new Intent(SignUpPage.this,HomePage.class);
                SignUpPage.this.startActivity(homPage);
                finish();
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean keepMeSignIn = false;
                if(validateSignUpDetails()) {
                    if(SignUpPage.this.keepMeSignIn.isChecked())
                        keepMeSignIn = true;
                    UserProfile userProfile = new UserProfile(userName.getText().toString(), mobileNo.getText().toString(), emailID.getText().toString(), password.getText().toString(), keepMeSignIn, 0);
                    signUpTask = new UserSignUpTask(userProfile);
                    View viewkey = SignUpPage.this.getCurrentFocus();
                    if (viewkey != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    showProgress(true);
                    signUpTask.execute((Void) null);

                }
            }
        });
    }

    private boolean validateSignUpDetails() {
        if (signUpTask != null)
            return false;

        userName.setError(null);
        mobileNo.setError(null);
        emailID.setError(null);
        password.setError(null);
        confirmPassword.setError(null);
        termsAndCondition.setError(null);
        String userNameData = userName.getText().toString();
        String mobileNoData = mobileNo.getText().toString();
        String emailIDData = emailID.getText().toString();
        String passwordData = password.getText().toString();
        String confirmPasswordData = confirmPassword.getText().toString();


        if (TextUtils.isEmpty(userNameData))
        {
            userName.setError(getString(R.string.empty_username));
            userName.requestFocus();
            return false;
        }
        if (!isUserNameProper(userNameData)){
            userName.setError(getString(R.string.incorrect_username));
            userName.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(mobileNoData)){
            mobileNo.setError(getString(R.string.empty_mobile_no));
            mobileNo.requestFocus();
            return false;
        }
        if (!isMobileProper(mobileNoData)){
            mobileNo.setError(getString(R.string.incorrect_mobile));
            mobileNo.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(emailIDData)){
            emailID.setError(getString(R.string.empty_email));
            emailID.requestFocus();
            return false;
        }
        if (!isEmailProper(emailIDData)){
            emailID.setError(getString(R.string.incorrect_email));
            emailID.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(passwordData)){
            password.setError(getString(R.string.empty_password));
            password.requestFocus();
            return false;
        }
        if (!isPasswordProper(passwordData)){
            password.setError(getString(R.string.incorrect_password));
            password.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(confirmPasswordData)){
            confirmPassword.setError(getString(R.string.empty_confirm_password));
            confirmPassword.requestFocus();
            return false;
        }
        if (!isConfirmPasswordProper(passwordData,confirmPasswordData)){
            confirmPassword.setError(getString(R.string.incorrect_confirm_password));
            confirmPassword.requestFocus();
            return false;
        }
        if (!termsAndCondition.isChecked()){
            termsAndCondition.setError(getString(R.string.error_terms_condition));
            termsAndCondition.requestFocus();
            return false;
        }
        return true;
    }

    private boolean isConfirmPasswordProper(String passwordData,String confirmPasswordData) {
        return passwordData.equals(confirmPasswordData)?true:false;
    }

    private boolean isPasswordProper(String passwordData) {
        return passwordData.length()<8?false:true;
    }

    private boolean isEmailProper(String emailIDData) {
        String emailPatter = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return emailIDData.matches(emailPatter);

    }

    private boolean isMobileProper(String mobile) {
        String mobilePattern = "[0-9]{10}";
        return mobile.matches(mobilePattern);
    }


    private boolean isUserNameProper(String userNameData) {
        String userIDPattern = "[a-zA-Z0-9_ ]+";
        return userNameData.matches(userIDPattern);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent homPage = new Intent(SignUpPage.this,HomePage.class);
        SignUpPage.this.startActivity(homPage);
        finish();
    }
    public class UserSignUpTask extends AsyncTask<Object, Object, Void> {
        UserProfile userProfile;

        public UserSignUpTask(UserProfile userProfile) {
            this.userProfile = userProfile;
        }

        @Override
        protected Void doInBackground(Object... voids) {
            try {


                MongoDBConnector mongoDBConnector = new MongoDBConnector(SignUpPage.this);
                if (!Settings.INTERNET_STATUS) {
                    Toast.makeText(SignUpPage.this, "No Network!", Toast.LENGTH_LONG).show();
                    return null;
                }
                mongoDBConnector.insertUserProfile(userProfile);
            } catch (Exception e) {
            }
            return null;
            //TODO: Insert in to mongoDB Atles
        }

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

            signUp.setVisibility(show ? View.GONE : View.VISIBLE);
            signUp.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    signUp.setVisibility(show ? View.GONE : View.VISIBLE);
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
            signUp.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
