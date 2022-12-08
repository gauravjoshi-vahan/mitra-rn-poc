package com.vahan.mitra_playstore.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.moengage.inapp.listeners.InAppMessageListener;
import com.moengage.inapp.model.MoEInAppCampaign;


/**
 * @author Umang Chamaria
 * Date: 2020-02-21
 */
public class InAppCallback extends InAppMessageListener {

  @Override public void onShown(@NonNull MoEInAppCampaign inAppCampaign) {
    super.onShown(inAppCampaign);
    Log.d("InApp Shown","InApp Shown: %s"+ inAppCampaign);
  }

  @Override public boolean onNavigation(@NonNull MoEInAppCampaign inAppCampaign) {
    Log.d("Navigation","Navigation Action: %s"+ inAppCampaign);
    // return true if the application is handling the navigation else false.
    return super.onNavigation(inAppCampaign);
  }

  @Override public void onClosed(@NonNull MoEInAppCampaign inAppCampaign) {
    super.onClosed(inAppCampaign);
    Log.d("In AppClosed","Navigation Action: %s"+ inAppCampaign);
  }

  @Override public void onCustomAction(@NonNull MoEInAppCampaign inAppCampaign) {
    super.onCustomAction(inAppCampaign);
    Log.d("In AppClosed","Navigation Action: %s"+ inAppCampaign);
  }

  @Override public void onSelfHandledAvailable(@NonNull MoEInAppCampaign inAppCampaign) {
    super.onSelfHandledAvailable(inAppCampaign);
    Log.d("SelfHandled","Navigation Action: %s"+ inAppCampaign);
  }
}