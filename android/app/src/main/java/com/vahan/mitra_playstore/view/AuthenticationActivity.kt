package com.vahan.mitra_playstore.view

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.ActivityAuthenticationBinding
import com.vahan.mitra_playstore.network.SharedViewModel
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.PrefrenceUtils
import com.vahan.mitra_playstore.utils.Utilities
import java.util.concurrent.Executor


class AuthenticationActivity : AppCompatActivity() {
    private lateinit var activityContext: Context
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private lateinit var binding: ActivityAuthenticationBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_authentication)
        activityContext = this
        executor = ContextCompat.getMainExecutor(activityContext)
        setPrompt()
        if (Utilities.isBiometricHardWareAvailable(activityContext)) {
            initBiometricPrompt(
                Constants.BIOMETRIC_AUTHENTICATION,
                Constants.BIOMETRIC_AUTHENTICATION_SUBTITLE,
                Constants.BIOMETRIC_AUTHENTICATION_DESCRIPTION,
                false
            )
        } else {
            //Fallback, use device password/pin
            if (Utilities.deviceHasPasswordPinLock(activityContext)) {
                initBiometricPrompt(
                    Constants.PASSWORD_PIN_AUTHENTICATION,
                    Constants.PASSWORD_PIN_AUTHENTICATION_SUBTITLE,
                    Constants.PASSWORD_PIN_AUTHENTICATION_DESCRIPTION,
                    true
                )
            }
        }
        if (this::promptInfo.isInitialized)
            biometricPrompt.authenticate(promptInfo)
        else {
            if (PrefrenceUtils.retriveDataInBoolean(
                    this@AuthenticationActivity,
                    Constants.CHECKFORPAYROLL
                )
            ) {
                Log.d("FUNNEL_ONE ", "handleIntents: ENTERA")
                Log.d("FUNNEL_ONE ", "handleIntents: ${intent.getStringExtra(Constants.TYPE)}")
                startActivity(Intent(this@AuthenticationActivity, MainActivity::class.java)
                    .putExtra(Constants.TYPE, intent.getStringExtra(Constants.TYPE)))
                finish()
            } else {
                Log.d("CLICKTWO", "initHandler: "+PrefrenceUtils.retriveData(this, intent.getStringExtra(Constants.TYPE)))
                startActivity(
                    Intent(
                        this@AuthenticationActivity,
                        HomeActivity::class.java
                    ).putExtra(Constants.TYPE, intent.getStringExtra(Constants.TYPE)))
                finish()
            }
        }
    }

    private fun setPrompt() {
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence,
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    createDialogue()
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult,
                ) {
                    super.onAuthenticationSucceeded(result)
                    Utilities.showSnackBar(
                        Constants.AUTHENTICATION_SUCCEEDED,
                        activityContext as AuthenticationActivity
                    )
                    if (PrefrenceUtils.retriveDataInBoolean(
                            this@AuthenticationActivity,
                            Constants.CHECKFORPAYROLL
                        )
                    ) {
                        Log.d("FUNNEL_ONE ", "handleIntents: ENTERA")
                        Log.d("FUNNEL_ONE ", "handleIntents: ${intent.getStringExtra(Constants.TYPE)}")
                        startActivity(Intent(this@AuthenticationActivity, MainActivity::class.java)
                            .putExtra(Constants.TYPE, intent.getStringExtra(Constants.TYPE)))
                        finish()
                    } else {
                        Log.d("CLICKTWO", "initHandler: "+PrefrenceUtils.retriveData(this@AuthenticationActivity, intent.getStringExtra(Constants.TYPE)))
                        startActivity(
                            Intent(
                                this@AuthenticationActivity,
                                HomeActivity::class.java
                            ).putExtra(Constants.TYPE, intent.getStringExtra(Constants.TYPE)))
                        finish()
                    }
                }


                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Utilities.showSnackBar(
                        Constants.AUTHENTICATION_FAILED,
                        activityContext as AuthenticationActivity
                    )
                }
            })
    }


    private fun initBiometricPrompt(
        title: String,
        subtitle: String,
        description: String,
        setDeviceCred: Boolean,
    ) {
        if (setDeviceCred) {
            promptInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                BiometricPrompt.PromptInfo.Builder()
                    .setTitle(title)
                    .setSubtitle(subtitle)
                    .setDescription(description)
                    .setAllowedAuthenticators(
                        BiometricManager.Authenticators.BIOMETRIC_STRONG or
                                DEVICE_CREDENTIAL or
                                BiometricManager.Authenticators.BIOMETRIC_WEAK
                    )
                    .setConfirmationRequired(false)
                    .build()
            } else {
                BiometricPrompt.PromptInfo.Builder()
                    .setTitle(title)
                    .setSubtitle(subtitle)
                    .setDescription(description)
                    .setAllowedAuthenticators(
                        BiometricManager.Authenticators.BIOMETRIC_STRONG or
                                DEVICE_CREDENTIAL or
                                BiometricManager.Authenticators.BIOMETRIC_WEAK
                    )
                    .setConfirmationRequired(false)
                    .build()
            }
        } else {
            promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setSubtitle(subtitle)
                .setDescription(description)
                .setAllowedAuthenticators(
                    BiometricManager.Authenticators.BIOMETRIC_STRONG or
                            DEVICE_CREDENTIAL or
                            BiometricManager.Authenticators.BIOMETRIC_WEAK
                )
                .setConfirmationRequired(false)
                .build()
        }
    }

    private fun createDialogue() {
        val builder = AlertDialog.Builder(this)
            .setCancelable(false)
        val view = LayoutInflater.from(this)
            .inflate(R.layout.cancell_layout_alert, null)
        val btnCancel = view.findViewById<RelativeLayout>(R.id.cancel)
        val btnUnlock = view.findViewById<RelativeLayout>(R.id.unlock)
        builder.setView(view)
        val alertDialog = builder.create()
        alertDialog.show()
        btnCancel.setOnClickListener {
            finish()
        }
        btnUnlock.setOnClickListener {
            biometricPrompt.authenticate(promptInfo)
        }

    }
}