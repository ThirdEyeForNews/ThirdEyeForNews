package com.demo.thirdeye;

import android.app.Dialog;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.thirdeye.utility.OtpViewText;
import com.demo.thirdeye.utility.Settings;
import com.demo.thirdeye.utility.SmsListener;
import com.demo.thirdeye.utility.SmsReceiver;

/**
 * Created by ammu on 8/25/2017.
 */

public class MobileVerificationActivity extends AppCompatActivity implements View.OnClickListener{

    TextView mvHeading,mvMobileNumber;
    Button submit,reSendOtp;
    OtpViewText otpViewText;
    CountDownTimer countDownTimer;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_confirmation);

        mvMobileNumber = (TextView) findViewById(R.id.mvMobileNumber);
        mvHeading = (TextView) findViewById(R.id.mvHeading);
        otpViewText = (OtpViewText) findViewById(R.id.otpTextBox);
        submit = (Button) findViewById(R.id.mvSubmit);
        reSendOtp = (Button) findViewById(R.id.mvOtpResent);
        mvMobileNumber.setText("OTP will be send to 8714443740. Click to edit");
        mvHeading.setTypeface(Settings.AGENCY_FB);
        submit.setTypeface(Settings.AGENCY_FB);
        reSendOtp.setTypeface(Settings.AGENCY_FB);
        mvMobileNumber.setTypeface(Settings.AGENCY_FB);

        mvMobileNumber.setOnClickListener(this);
        submit.setOnClickListener(this);
        reSendOtp.setOnClickListener(this);
        final SmsReceiver smsReceiver = new SmsReceiver();
        registerReceiver(smsReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
        smsReceiver.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String sender,String messageText) {
                Toast.makeText(MobileVerificationActivity.this, "SMS detected and setting OTP", Toast.LENGTH_LONG).show();
                otpViewText.disableKeypad();

                final Dialog dialog= new Dialog(MobileVerificationActivity.this);
                dialog.setContentView(R.layout.custom_dialog_box);
                final EditText cEditText = (EditText) dialog.findViewById(R.id.cEditText);
                Button cButton = (Button) dialog.findViewById(R.id.cButton);

                cEditText.setTypeface(Settings.AGENCY_FB);
                cButton.setTypeface(Settings.AGENCY_FB);

                cEditText.setText("1234");
                cButton.setText("APPROVE");

                cButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Toast.makeText(MobileVerificationActivity.this,"OTP verified",Toast.LENGTH_SHORT);
                        //Settings.USER_PROFILE.setMobileNumber(cEditText.getText().toString());
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        reSendOtp.setEnabled(false);
        countDownTimer = new CountDownTimer(300000,1000){
            /**
             * Callback fired on regular interval.
             *
             * @param millisUntilFinished The amount of time until finished.
             */
            @Override
            public void onTick(long millisUntilFinished) {
                reSendOtp.setTextColor(getResources().getColor(R.color.colorBlack));
                reSendOtp.setText(millisUntilFinished/60000+":"+(millisUntilFinished%60000)/1000);
            }

            /**
             * Callback fired when the time is up.
             */
            @Override
            public void onFinish() {
                reSendOtp.setEnabled(true);
                reSendOtp.setText("RE-SEND OTP");
            }
        };
        countDownTimer.start();

    }
    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v==submit){
            String otp = otpViewText.getOTP();
            Toast.makeText(this,otp,Toast.LENGTH_LONG).show();
        }else if (v==reSendOtp){
            countDownTimer.start();
        }else if(v == mvMobileNumber){
            final Dialog dialog= new Dialog(MobileVerificationActivity.this);
            dialog.setContentView(R.layout.custom_dialog_box);
            final EditText cEditText = (EditText) dialog.findViewById(R.id.cEditText);
            Button cButton = (Button) dialog.findViewById(R.id.cButton);

            cEditText.setTypeface(Settings.AGENCY_FB);
            cButton.setTypeface(Settings.AGENCY_FB);

            cEditText.setText("8714443740");
            cButton.setText("OK");

            cButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MobileVerificationActivity.this,"Mobile Number updated",Toast.LENGTH_SHORT);
                    //Settings.USER_PROFILE.setMobileNumber(cEditText.getText().toString());
                    dialog.dismiss();
                }
            });

            dialog.show();

        }
    }

}
