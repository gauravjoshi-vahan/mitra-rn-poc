package com.vahan.mitra_playstore.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;
import com.vahan.mitra_playstore.utils.Constants;

public class SmsReceiver extends BroadcastReceiver {

    private OTPReceiveListener otpListener;

    /**
     * @param otpListener
     */
    public void setOTPListener(OTPReceiveListener otpListener) {
        this.otpListener = otpListener;
    }


    /**
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);
            switch (status.getStatusCode()) {
                case CommonStatusCodes.SUCCESS:

                    //This is the full message
                    String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);

                    /*<#> Your ExampleApp code is: 123ABC78
                    FA+9qCX9VSu*/

                    //Extract the OTP code and send to the listener

                    if (otpListener != null) {
                        otpListener.onOTPReceived(message);
                    }
                    break;
                case CommonStatusCodes.TIMEOUT:
                    // Waiting for SMS timed out (5 minutes)
                    if (otpListener != null) {
                        otpListener.onOTPTimeOut();
                    }
                    break;

                case CommonStatusCodes.API_NOT_CONNECTED:
                    if (otpListener != null) {
                        otpListener.onOTPReceivedError(Constants.API_NOT_CONNECTED);
                    }
                    break;

                case CommonStatusCodes.NETWORK_ERROR:
                    if (otpListener != null) {
                        otpListener.onOTPReceivedError(Constants.NETWORK_ERROR);
                    }
                    break;

                case CommonStatusCodes.ERROR:
                    if (otpListener != null) {
                        otpListener.onOTPReceivedError(Constants.SOME_THING_WENT_WRONG);
                    }

                    break;

            }
        }else if (intent.getAction().equals(Constants.LOCATION_UPDATE)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context, LocationService.class));
            } else {
                context.startService(new Intent(context, LocationService.class));
            }
        }
    }

    /**
     *
     */
    public interface OTPReceiveListener {

        void onOTPReceived(String otp);

        void onOTPTimeOut();

        void onOTPReceivedError(String error);
    }
}
