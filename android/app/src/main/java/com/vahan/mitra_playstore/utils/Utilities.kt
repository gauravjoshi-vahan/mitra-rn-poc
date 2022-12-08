package com.vahan.mitra_playstore.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_WEAK
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.vahan.mitra_playstore.R
import kotlinx.coroutines.*

object Utilities {
    fun showSnackBar(snackTitle: String?, act: Activity) {
        try {
            val view1 = act.findViewById<ConstraintLayout>(R.id.constraintlayoutMain)!!
            val snackbar: Snackbar = Snackbar.make(view1, snackTitle!!, Snackbar.LENGTH_SHORT)
            val view: View = snackbar.view

            if (!act.isFinishing)
                snackbar.show()

            val txtv = view.findViewById(R.id.snackbar_text) as TextView
            txtv.gravity = Gravity.CENTER_HORIZONTAL
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
    @SuppressLint("WrongConstant")
    fun isBiometricHardWareAvailable(con: Context): Boolean {
        var result = false
        val biometricManager = BiometricManager.from(con)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            when (biometricManager.canAuthenticate(BiometricManager.Authenticators.DEVICE_CREDENTIAL or BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
                BiometricManager.BIOMETRIC_SUCCESS -> result = true
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> result = false
                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> result = false
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> result = false
                BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED ->
                    result = true
                BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED ->
                    result = true
                BiometricManager.BIOMETRIC_STATUS_UNKNOWN ->
                    result = false
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or BIOMETRIC_WEAK)) {
                    BiometricManager.BIOMETRIC_SUCCESS -> result = true
                    BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> result = false
                    BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> result = false
                    BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> result = false
                    BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> result = false
                    BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> result = false
                    BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> result = false
                }
            }
        }
        return result
    }
    fun deviceHasPasswordPinLock(con: Context): Boolean {
        val keymgr = con.getSystemService(AppCompatActivity.KEYGUARD_SERVICE) as KeyguardManager
        if (keymgr.isKeyguardSecure)
            return true
        return false
    }
}